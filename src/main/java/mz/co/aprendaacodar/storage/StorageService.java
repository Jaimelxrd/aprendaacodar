package mz.co.aprendaacodar.storage;
import org.springframework.web.multipart.MultipartFile;
public interface StorageService {String upload(String folder,MultipartFile file,boolean isPublic);String url(String key,boolean isPublic);}
