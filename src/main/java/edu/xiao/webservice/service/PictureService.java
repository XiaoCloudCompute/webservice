package edu.xiao.webservice.service;

import edu.xiao.webservice.model.Picture;
import edu.xiao.webservice.model.User;
import edu.xiao.webservice.repository.PictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import static org.apache.http.entity.ContentType.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PictureService {

    @Value("${aws.bucket_name}")
    private String bucketName;

    @Autowired
    private FileStoreService fileStoreService;

    @Autowired
    private PictureRepository pictureRepository;

    public Picture upload(MultipartFile file, User user) {
        //check if the file is empty
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }
        //Check if the file is an image
        if (!Arrays.asList(IMAGE_PNG.getMimeType(),
                IMAGE_BMP.getMimeType(),
                IMAGE_GIF.getMimeType(),
                IMAGE_JPEG.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException("FIle uploaded is not an image");
        }
        //get file metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        Optional<Picture> opPic = pictureRepository.findByUserId(user.getId().toString());
        Picture pic = new Picture();

        if (opPic.isPresent()) {
            // delete first
            pic = opPic.get();
            this.delete(user);
        }
        //Save Image in S3
        String fileName = String.format("%s", file.getOriginalFilename());
        String path = String.format("%s/%s", user.getId().toString(), fileName);
        try {
            fileStoreService.upload(this.bucketName, path, Optional.of(metadata), file.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file", e);
        }
        //Save to database
        pic.setUrl(String.format("%s/%s", this.bucketName, path));
        pic.setUserId(user.getId().toString());
        pic.setFileName(fileName);
        pictureRepository.save(pic);
        return pictureRepository.findByUserId(user.getId().toString()).orElseThrow();
    }

    public Picture get(User user) {
        return pictureRepository.findByUserId(user.getId().toString()).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User: %s, not found", user.getUsername()))
        );
    }

    public void delete(User user) {
        Picture pic = pictureRepository.findByUserId(user.getId().toString()).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Pictures of user: %s, not found", user.getUsername()))
        );
        String bucketName = pic.getUrl().split("/")[0];
        String path = pic.getUrl().split("/", 2)[1];
        fileStoreService.delete(bucketName, path);
        pictureRepository.delete(pic);
    }
}
