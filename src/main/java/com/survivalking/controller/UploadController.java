package com.survivalking.controller;


import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.survivalking.domain.AttachFileDTO;

import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnailator;

@Controller
@Log4j
public class UploadController {
	
	private String getFolder() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Date date = new Date();
		
		String str = dateFormat.format(date);
		
		System.out.println("폴더명 : " + str);
		
		return str.replace("-", File.separator);
	}
	
	private boolean checkImageType(File file) {
		try {
			String contentType = Files.probeContentType(file.toPath());
			
			return contentType.startsWith("image");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	@GetMapping("/uploadForm")
	public void uploadForm() {
		//log.info("upload form");
	}
	
	@GetMapping("/uploadAjax")
	public void uploadAjax() {
		//log.info("upload Ajax");
	}
	
	@PostMapping("/uploadFormAction")
	public void uploadFormPost(MultipartFile[] uploadFile, Model model) {
		String uploadFolder = "D:\\Spring_Pjt\\upload\\temp";
		
		for(MultipartFile file : uploadFile) {
			File saveFile = new File(uploadFolder, file.getOriginalFilename());
			
			try {
				file.transferTo(saveFile);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
			
	}
	
	/*
	 * @PostMapping("/uploadAjaxAction") public void uploadAjaxPost(MultipartFile[]
	 * uploadFile, Model model) { String uploadFolder =
	 * "D:\\Spring_Pjt\\upload\\temp";
	 * 
	 * // make folder File uploadPath = new File(uploadFolder, getFolder());
	 * 
	 * // yyyy/MM/dd 이름으로 파일 생성 if(!uploadPath.exists()) { uploadPath.mkdirs(); }
	 * 
	 * for(MultipartFile file : uploadFile) { String uploadFileName =
	 * file.getOriginalFilename();
	 * 
	 * uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") +
	 * 1);
	 * 
	 * UUID uuid = UUID.randomUUID();
	 * 
	 * uploadFileName = uuid.toString() + "_" + uploadFileName;
	 * 
	 * //File saveFile = new File(uploadFolder, uploadFileName);
	 * 
	 * 
	 * try { File saveFile = new File(uploadPath, uploadFileName);
	 * 
	 * file.transferTo(saveFile);
	 * 
	 * if(checkImageType(saveFile)) { FileOutputStream thumbnail = new
	 * FileOutputStream(new File(uploadPath, "s_" + uploadFileName));
	 * 
	 * Thumbnailator.createThumbnail(file.getInputStream(), thumbnail, 100, 100);
	 * 
	 * thumbnail.close(); } } catch(Exception e) { e.printStackTrace(); } }
	 * 
	 * }
	 */
	
	@PostMapping(value = "/uploadAjaxAction", produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ResponseEntity<List<AttachFileDTO>> uploadAjaxPost(MultipartFile[] uploadFile) {
		List<AttachFileDTO> list = new ArrayList<>();
		
		String uploadFolder = "D:\\Spring_Pjt\\upload\\temp";
		
		String uploadFolderPath = getFolder();
		
		// make folder
		File uploadPath = new File(uploadFolder, uploadFolderPath);
		
		// yyyy/MM/dd 이름으로 파일 생성
		if(!uploadPath.exists()) {
			uploadPath.mkdirs();
		}
		
		for(MultipartFile file : uploadFile) {
			AttachFileDTO attachFileDTO = new AttachFileDTO();
			
			String uploadFileName = file.getOriginalFilename();
			
			uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") + 1);
			
			attachFileDTO.setFileName(uploadFileName);
			
			UUID uuid = UUID.randomUUID();
			
			uploadFileName = uuid.toString() + "_" + uploadFileName;
			
			//File saveFile = new File(uploadFolder, uploadFileName);
			
			
			try {
				File saveFile = new File(uploadPath, uploadFileName);
				
				file.transferTo(saveFile);
				
				attachFileDTO.setUuid(uuid.toString());
				attachFileDTO.setUploadPath(uploadFolderPath);
				
				if(checkImageType(saveFile)) {
					attachFileDTO.setImage(true);
					
					FileOutputStream thumbnail = new FileOutputStream(new File(uploadPath, "s_" + uploadFileName));
					
					Thumbnailator.createThumbnail(file.getInputStream(), thumbnail, 100, 100);
					
					thumbnail.close();
				}
				
				// list에 추가
				list.add(attachFileDTO);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@GetMapping("/display")
	@ResponseBody
	public ResponseEntity<byte[]> getFile(String fileName){
		log.info("Display File Name : " + fileName);
		
		File file = new File("D:\\Spring_Pjt\\upload\\temp\\" + fileName);
		
		ResponseEntity<byte[]> result = null;
		
		try {
			HttpHeaders header = new HttpHeaders();
			
			header.add("Content-Type", Files.probeContentType(file.toPath()));
			
			result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	} // end of getFile
	
	@GetMapping(value="/download", produces= MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public ResponseEntity<Resource> downloadFile(@RequestHeader("User-Agent") String userAgent,  String fileName){
		
		Resource resource = new FileSystemResource("D:\\Spring_Pjt\\upload\\temp\\" + fileName);
		
		//log.info(resource);
		
		if(resource.exists() == false) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		String resourceName = resource.getFilename();
		
		//removeUUID
		String resourceOriginalName = resourceName.substring(resourceName.indexOf("_") + 1);
		
		//log.info("리소스 명 : " + resourceOriginalName);
		
		HttpHeaders headers = new HttpHeaders();
		
		try {
			String downloadName = null;
			
			if(userAgent.contains("Trident")) {
				log.info("IE Browser");
				
				downloadName = URLEncoder.encode(resourceOriginalName,"UTF-8").replaceAll("\\+", " ");
			}
			else if(userAgent.contains("Edge")) {
				log.info("Edge browser");
				
				downloadName = URLEncoder.encode(resourceOriginalName,"UTF-8");
			}
			else {
				log.info("Chrome Browser");
				downloadName = new String(resourceOriginalName.getBytes("UTF-8"),"ISO-8859-1");
			}
			
			headers.add("Content-Disposition", "attachment; filename=" + downloadName);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
	}
	
	@PostMapping("/deleteFile")
	@ResponseBody
	public ResponseEntity<String> deleteFile(String fileName, String type){
		
		File file;
		
		try {
			file = new File("D:\\Spring_Pjt\\upload\\temp\\" + URLDecoder.decode(fileName,"UTF-8"));
			
			log.info("deleteFile2: " + file.getAbsolutePath());
			
			file.delete();
			
			if(type.equals("image")) {
				String largeFileName = file.getAbsolutePath().replace("s_", "");
				
				log.info("lageFileName : " + largeFileName);
				
				file = new File(largeFileName);
				
				if(file.exists()) file.delete();
			}
		}
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<String>("Deleted", HttpStatus.OK);
	}
	
} // end of Class
