<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-\\W3C\\DTD HTML 4.01 Transitional\\EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<div class='bigPictureWrapper'>
	<div class='bigPicture'>
	</div>
</div>

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
		align-context : center;
		text-align : center;
	}
	
	.uploadResult ul li img{
		width:20%;
	}
	
	.bigPictureWrapper{
		position:absolute;
		display:none;
		justify-content:center;
		align-times:center;
		top:0%;
		width:100%;
		height:100%;
		background-color:yellow;
		z-index:100;
		background:rgba(255,255,255,0.5);
	}
	
	.bigPicture{
		position:relative;
		display:flex;
		justify-content:center;
		align-times:center;
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
function showImage(fileCallPath){
	//alert(fileCallPath);
	
	$(".bigPictureWrapper").css("display","flex").show();
	
	$(".bigPicture")
	.html("<img src= '/display?fileName=" + encodeURI(fileCallPath) + "'>'")
	.animate({width:'100%', height: '100%'}, 1000);
	
	$(".bigPictureWrapper").on("click",function(e){
		$(".bigPicture").animate({width:'0%', height: '0%'}, 1000);
		setTimeout(function(){
			$(".bigPictureWrapper").hide();
		},1000);
	});
}

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
				var fileCallPath = encodeURIComponent(obj.uploadPath + "/" + obj.uuid + "_" + obj.fileName);
				
				str += "<li><div><a href='/download?fileName=" + fileCallPath + "'><img src='/resources/img/attach.png'>" + obj.fileName + 
						"</a><span data-file=\'" + fileCallPath + "\' data-type='file'> x </span></div></li>";
			}
			else{
				var fileCallPath = encodeURIComponent(obj.uploadPath + "/s_" + obj.uuid + "_" + obj.fileName);
				
				var originPath = obj.uploadPath + "\\" + obj.uuid + "_" + obj.fileName;
				
				//console.log("originPath : " + originPath);
				
				originPath = originPath.replace(new RegExp(/\\/g), "/");
				
				//console.log("originPath : " + originPath);
				
				//str += "<li><img src='/display?fileName=" + fileCallPath + "'></li>";
				str += "<li><a href=\"javascript:showImage(\'" + originPath + "\')\"><img src='/display?fileName=" + fileCallPath + "'></a>" + 
						"<span data-file=\'" + fileCallPath + "\' data-type='image'> x </span></li>";
				
				console.log("로그 : " + str);
			}
		});
		
		uploadResult.append(str);
	}
	
	$("#uploadBtn").on("click",function(e){
		var formData = new FormData();
		
		var inputFile = $("input[name='uploadFile']");
		
		var files = inputFile[0].files;
		
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
				
				showUploadedFile(result); // 업로드 한 파일 보여주기
				
				$(".uploadDiv").html(cloneObj.html()); // 파일 업로드시 업로드 할 리스트 지워줌
				
			}
		}); // end of ajax	
	}); // end of upload Btn
	
	
	$(".uploadResult").on("click", "span", function(e){
		var targetFile = $(this).data("file");
		var type = $(this).data("type");
		console.log(targetFile);
		
		$.ajax({
			url: '/deleteFile',
			data : {fileName : targetFile, type:type},
			type : 'POST',
			dataType : 'text',
			success : function(result){
				alert(result);
			}
		}); // end of ajax
		
	}); // end of uploadResult Span Btn
	
});
</script>
</body>
</html>

