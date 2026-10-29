package com.hk.ecom_monolithic.services;

import com.hk.ecom_monolithic.DTO.ProductDTO;
import com.hk.ecom_monolithic.DTO.ProductResponse;
import com.hk.ecom_monolithic.exceptions.APIException;
import com.hk.ecom_monolithic.exceptions.ResourceNotFoundException;
import com.hk.ecom_monolithic.model.Category;
import com.hk.ecom_monolithic.model.Product;
import com.hk.ecom_monolithic.repositories.CategoryRepository;
import com.hk.ecom_monolithic.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.images}")
    private String path;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {

        Category category = categoryRepository.findById(categoryId).
                orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for(Product product : products){
            if(product.getProductName().equals(productDTO.getProductName())){
                isProductNotPresent = false;
                break;
            }
        }

        if(isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);

            product.setCategory(category);
            product.setImage("default.png");
            double specialPrice = productDTO.getPrice() - (productDTO.getPrice() * (productDTO.getDiscount() * 0.01));
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }else{
            throw new APIException("Product already exists!!");
        }

    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productsPage = productRepository.findAll(pageDetails);
        List<Product> products = productsPage.getContent();

        if(products.isEmpty()){
            throw new APIException("No products Exist!");
        }

        ProductResponse productResponse =  toProductResponse(products);

        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setLastPage(productsPage.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Category category = categoryRepository.findById(categoryId).
                orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productsPage = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
        List<Product> products = productsPage.getContent();

        if(products.isEmpty()){
            throw new APIException("No products Exist with categoryId: " + categoryId);
        }

        ProductResponse productResponse =  toProductResponse(products);

        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setLastPage(productsPage.isLast());

        return productResponse;


    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productsPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
        List<Product> products = productsPage.getContent();

        if(products.isEmpty()){
            throw new APIException("No products Exist with keyword: " + keyword);
        }

        ProductResponse productResponse =  toProductResponse(products);
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setLastPage(productsPage.isLast());

        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {

        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        productFromDb.setProductName(productDTO.getProductName());
        productFromDb.setDescription(productDTO.getDescription());
        productFromDb.setQuantity(productDTO.getQuantity());
        productFromDb.setPrice(productDTO.getPrice());
        productFromDb.setDiscount(productDTO.getDiscount());
        double specialPrice = productDTO.getPrice() - (productDTO.getPrice() *  (productDTO.getDiscount() * 0.01));
        productFromDb.setSpecialPrice(specialPrice);

        //productFromDb.setSpecialPrice(productDTO.getSpecialPrice());

        Product updatedProduct = productRepository.save(productFromDb);
        return modelMapper.map(updatedProduct, ProductDTO.class);

    }

    @Override
    public ProductDTO deleteProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        // get product from db
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        //upload image to server
        //get file name of uploaded image
        String fileName = fileService.uploadImage(path, image);

        //update the product with new file name of the image
        productFromDb.setImage(fileName);

        //save updated product
        Product  updatedProduct = productRepository.save(productFromDb);

        //return productDTO
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    private ProductResponse toProductResponse(List<Product> products){

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        return productResponse;
    }
}