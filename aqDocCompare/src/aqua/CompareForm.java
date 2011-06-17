package aqua;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class CompareForm {
	
	private MultipartFile original;
	private MultipartFile migration;
	
	public MultipartFile getOriginal() {
		return original;
	}
	
	public MultipartFile getMigration() {
		return migration;
	}
	
	@Autowired
	public void setOriginal(MultipartFile original) {
		this.original = original;
	}
	@Autowired
	public void setMigration(MultipartFile migration) {
		this.migration = migration;
	}

}
