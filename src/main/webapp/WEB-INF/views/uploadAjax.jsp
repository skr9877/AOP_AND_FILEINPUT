<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-\\W3C\\DTD HTML 4.01 Transitional\\EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<style>
	.uploadResult{
		width:100%;
		background-color:yellow;
	}
	
	.uploadResult ul{
		display:flex;
		flex-flow:row;
		justify-content:center;
		align-items:center;
	}
	
	.uploadResult ul li{
		list-style:none;
		padding:10px;
	}
	
	.uploadResult ul li img{
		width:20%;
	}
</style>

<h1>Upload with Ajax</h1>

<div class='uploadDiv'>
	<input type="file" name="uploadFile" multiple>
</div>

<div class='uploadResult'>
<ul>
<!-- JS will Insert li -->
</ul>
</div>

<button id="uploadBtn">업로드</button>


<script
  src="https://code.jquery.com/jquery-3.5.1.js"
  integrity="sha256-QWo7LDvxbWT2tbbQ97B53yJnYU3WhH/C8ycbRAkjPDc="
  crossorigin="anonymous"></script>
  
<script>
$(document).ready(function(){
	var regex = new RegExp("(.*?)\.(exe|sh|zip|alz|asp)$");
	var maxSize = 5242880; // 5MB
	var cloneObj = $(".uploadDiv").clone(); // Input 파일의 DIV 클론
	var uploadResult = $(".uploadResult ul");
	
	function checkExtension(fileName, fileSize){
		if(fileSize > maxSize){
			alert("파일 사이즈 초과");
			return false;
		}
		
		if(regex.test(fileName)){
			alert("해당 종류의 파일을 업로드 할 수 없습니다.");
			return false;
		}
		
		return true;
	}
	
	function showUploadedFile(uploadResultArr){
		var str = "";
		
		$(uploadResultArr).each(function(i, obj){
			if(!obj.image){
				str += "<li><img src='/resources/img/attach.png'>" + obj.fileName + "</li>";
			}
			else{
				console.log("로그 이름: " + obj.fileName);
				var fileCallPath = encodeURIComponent(obj.uploadPath + "/s_" + obj.uuid + "_" + obj.fileName);
				
				//str += "<li>" + obj.fileName + "</li>";
				str += "<li><img src='/display?fileName=" + fileCallPath + "'></li>";
				
				console.log("로그 : " + str);
			}
		});
		
		uploadResult.append(str);
	}
	
	$("#uploadBtn").on("click",function(e){
		var formData = new FormData();
		
		var inputFile = $("input[name='uploadFile']");
		
		var files = inputFile[0].files;
		
		console.log(files);
		
		for(var i = 0; i < files.length; ++i){
			if(!checkExtension(files[i].name, files[i].size)){
				return false;
			}
			
			formData.append("uploadFile",files[i]);
		}
		
		$.ajax({
			url: '/uploadAjaxAction',
			processData : false,
			contentType : false,
			data : formData,
			type : 'POST',
			dataType : 'json',
			success : function(result){
				alert("업로드 완료");
				
				showUploadedFile(result);
				
				$(".uploadDiv").html(cloneObj.html());
				
			}
		}); // end of ajax	
	}); // end of upload Btn
	
	
});
</script>
</body>
</html>

