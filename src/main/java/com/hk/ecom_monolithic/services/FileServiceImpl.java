package com.hk.ecom_monolithic.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        //get file name of original file
        String originalFileName = file.getOriginalFilename();

        //generate a unique file name
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));
        String filePath = path + File.separator + fileName;

        //check if path exists or else create
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }

        //Upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        //returning the fileName
        return fileName;

    }

}
