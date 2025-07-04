package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dto.MemoryCreateDto;
import com.java.web.virtual.time.capsule.enums.MemoryType;
import com.java.web.virtual.time.capsule.service.MemoryService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/memories")
public class MemoryController {

    private final MemoryService memoryService;
    
    @Value("${timecapsule.upload.dir:/app/uploads}")
    private String uploadDirectory;

    public MemoryController(final MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) 
    public ResponseEntity<?> addNewMemoryToCapsule(
        @NotNull @RequestParam("capsuleId") Long capsuleId, 
        @NotNull @RequestParam("description") String description, 
        @NotNull @RequestParam("type") MemoryType type,         
        @NotNull @RequestPart("content") MultipartFile content, 
        Principal principal
    ) throws IOException {
        log.info("Adding new memory to capsule for capsuleId: {} by user: {}", capsuleId, principal.getName());

        MemoryCreateDto memoryDto = new MemoryCreateDto();
        memoryDto.setDescription(description);
        memoryDto.setType(type);
        memoryDto.setContent(content); 

        Long memoryId = memoryService.saveMemory(capsuleId, memoryDto, principal.getName());
        URI location = URI.create("/api/v1/memories/" + memoryId);
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}/addMemory")
    public ResponseEntity<?> addMemoryToCapsule(@PathVariable Long id, @RequestParam Long capsuleId, Principal principal) {
        Long memoryId = memoryService.addMemoryToCapsule(capsuleId, id,principal.getName());
        URI location = URI.create("/api/v1/memories/" + memoryId);
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}/removeMemory")
    public ResponseEntity<?> removeMemoryFromCapsule(@PathVariable Long id, @RequestParam Long capsuleId, Principal principal) {
        memoryService.deleteMemory(capsuleId,id,principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/content/{filename:.+}") 
    public ResponseEntity<Resource> serveMemoryContent(@PathVariable String filename, Principal principal) {
        log.info("Received request to serve content: {} for user: {}", filename, principal.getName());
        try {
            Path filePath = Paths.get(uploadDirectory).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            log.info("Attempting to load file from absolute path: {}", filePath.toAbsolutePath().toString());
            log.info("Resource exists: {}", resource.exists());
            log.info("Resource is readable: {}", resource.isReadable());

            if (resource.exists() && resource.isReadable()) {
                String contentType = null;
                try {
                    contentType = Files.probeContentType(filePath);
                    log.info("Files.probeContentType returned: {}", contentType);
                } catch (IOException e) {
                    log.warn("Failed to probe content type for {}: {}", filename, e.getMessage());
                }

                if (contentType == null || contentType.equals(MediaType.APPLICATION_OCTET_STREAM_VALUE)) {
                    String fileExtension = "";
                    int dotIndex = filename.lastIndexOf('.');
                    if (dotIndex > 0 && dotIndex < filename.length() - 1) {
                        fileExtension = filename.substring(dotIndex + 1).toLowerCase();
                    }
                    switch (fileExtension) {
                        case "jpg": case "jpeg": contentType = MediaType.IMAGE_JPEG_VALUE; break;
                        case "png": contentType = MediaType.IMAGE_PNG_VALUE; break;
                        case "gif": contentType = MediaType.IMAGE_GIF_VALUE; break;
                        case "bmp": contentType = "image/bmp"; break; 
                        case "webp": contentType = "image/webp"; break; 

                        case "pdf": contentType = MediaType.APPLICATION_PDF_VALUE; break;
                        case "txt": contentType = MediaType.TEXT_PLAIN_VALUE; break; 
                        case "html": case "htm": contentType = MediaType.TEXT_HTML_VALUE; break; 

                        case "mp4": contentType = "video/mp4"; break;
                        case "webm": contentType = "video/webm"; break;
                        case "ogg": contentType = "video/ogg"; break; 

                        case "mp3": contentType = "audio/mpeg"; break;
                        case "wav": contentType = "audio/wav"; break;
                        case "ogg_audio": contentType = "audio/ogg"; break;
                        default:
                            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; 
                            log.warn("Using default Content-Type {} for unknown extension: {}", contentType, fileExtension);
                    }
                    log.info("Determined content type by extension fallback: {} -> {}", fileExtension, contentType);
                }

                
                if (contentType.startsWith("text/") || contentType.equals(MediaType.APPLICATION_JSON_VALUE)) {
                    contentType += ";charset=UTF-8";
                    log.info("Appended charset=UTF-8 to Content-Type: {}", contentType);
                }

                log.info("Final Content-Type for {}: {}", filename, contentType);

                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            } else {
                log.warn("File not found or not readable: {}", filePath.toAbsolutePath().toString());
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            log.error("Error serving memory content {}: {}", filename, e.getMessage(), e); 
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) { 
            log.error("An unexpected error occurred while serving memory content {}: {}", filename, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
