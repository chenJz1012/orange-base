package com.orange.base.core.api.common;

import com.alibaba.fastjson.JSONObject;
import com.orange.base.common.utils.CompressUtil;
import com.orange.base.common.utils.FileManagerUtil;
import com.orange.base.common.utils.FileUtil;
import com.orange.base.common.utils.ResponseUtil;
import com.orange.base.core.dto.FileDto;
import com.orange.base.security.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by cgj on 2015/12/8.
 */
@Controller
@RequestMapping("/api/fileManager")
public class FileManagerController {

    @Value("${upload.folder}")
    private String uploadFolder;

    @RequestMapping("/files")
    public
    @ResponseBody
    Object files(HttpServletRequest request, @RequestParam(value = "path", defaultValue = "") String path) {
        String rootPath = request.getSession().getServletContext().getRealPath("/") + uploadFolder + "/" + SecurityUtil
                .getCurrentUserName() + "/";
        File rootFolder = new File(rootPath);
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }
        // 文件保存目录URL
        String rootUrl = request.getContextPath() + "/" + uploadFolder + "/" + SecurityUtil.getCurrentUserName() + "/";
        // 最后一个字符不是/
        if (!"".equals(path) && !path.endsWith("/")) {
            path += "/";
        }
        // 根据path参数，设置各路径和URL
        String currentPath = rootPath + path;
        String currentUrl = rootUrl + path;
        String currentDirPath = path;
        String moveUpDirPath = "";
        if (!"".equals(path)) {
            String str = currentDirPath.substring(0, currentDirPath.length() - 1);
            moveUpDirPath = str.lastIndexOf("/") >= 0 ? str.substring(0, str.lastIndexOf("/") + 1) : "";
        }

        // 不允许使用..移动到上一级目录
        if (path.indexOf("..") >= 0) {
            return ResponseUtil.error("Access is not allowed.");
        }
        // 目录不存在或不是目录
        File currentPathFile = new File(currentPath);
        if (!currentPathFile.isDirectory()) {
            return ResponseUtil.error("Directory does not exist.");
        }
        // 遍历目录取的文件信息
        List<FileDto> fileList = new ArrayList<FileDto>();
        if (currentPathFile.listFiles() != null) {
            for (File file : currentPathFile.listFiles()) {
                FileDto fileDto = new FileDto();
                Hashtable<String, Object> hash = new Hashtable<String, Object>();
                String fileName = file.getName();
                if (file.isDirectory()) {
                    fileDto.setIsDirectory(true);
                    fileDto.setHashFile(file.listFiles() != null);
                    fileDto.setFileSize(0L);
                    fileDto.setType("");
                } else if (file.isFile()) {
                    String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                    fileDto.setIsDirectory(false);
                    fileDto.setHashFile(false);
                    fileDto.setFileSize(file.length());
                    fileDto.setType(fileExt);
                }
                fileDto.setName(fileName);
                fileDto.setPath(file.getPath());
                fileDto.setLastModifed(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
                fileList.add(fileDto);
            }
        }
        Collections.sort(fileList);
        JSONObject msg = new JSONObject();
        msg.put("moveUpDirPath", moveUpDirPath);
        msg.put("currentDirPath", currentDirPath);
        msg.put("currentUrl", currentUrl);
        msg.put("total", fileList.size());
        msg.put("files", fileList);
        return ResponseUtil.success(msg);
    }

    @RequestMapping(value = "/createFolder", method = RequestMethod.POST)
    public
    @ResponseBody
    Object createFolder(HttpServletRequest httpServletRequest, @RequestParam(value = "dirPath") String dirPath) {
        String rootPath =
                httpServletRequest.getSession().getServletContext().getRealPath("/") + uploadFolder + "/" + SecurityUtil
                        .getCurrentUserName() + "/";
        String wholeRealPath = rootPath + dirPath;
        FileManagerUtil.mkDir(wholeRealPath);
        return ResponseUtil.success();
    }

    @RequestMapping(value = "/rename", method = RequestMethod.POST)
    public
    @ResponseBody
    Object rename(HttpServletRequest httpServletRequest, @RequestParam(value = "folderPath") String folderPath,
            @RequestParam(value = "oldName") String oldName, @RequestParam(value = "newName") String newName) {
        String rootPath =
                httpServletRequest.getSession().getServletContext().getRealPath("/") + uploadFolder + "/" + SecurityUtil
                        .getCurrentUserName() + "/";
        String wholeRealPath = rootPath + folderPath;
        return FileManagerUtil.renameFile(wholeRealPath, oldName, newName);
    }

    @RequestMapping(value = "/deleteFolder", method = RequestMethod.POST)
    public
    @ResponseBody
    Object deleteFolder(HttpServletRequest httpServletRequest, @RequestParam(value = "dirPath") String dirPath) {
        String rootPath =
                httpServletRequest.getSession().getServletContext().getRealPath("/") + uploadFolder + "/" + SecurityUtil
                        .getCurrentUserName() + "/";
        String wholeRealPath = rootPath + dirPath;
        FileManagerUtil.delFolder(wholeRealPath);
        return ResponseUtil.success();
    }

    @RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
    public
    @ResponseBody
    Object deleteFile(HttpServletRequest httpServletRequest, @RequestParam(value = "filePath") String filePath) {
        String rootPath =
                httpServletRequest.getSession().getServletContext().getRealPath("/") + uploadFolder + "/" + SecurityUtil
                        .getCurrentUserName() + "/";
        String wholeRealPath = rootPath + filePath;
        FileManagerUtil.delFile(wholeRealPath);
        return ResponseUtil.success();
    }

    @RequestMapping("/download")
    public void downloadFile(HttpServletResponse response, HttpServletRequest httpServletRequest,
            @RequestParam(value = "folderPath") String folderPath, @RequestParam(value = "fileName") String fileName)
            throws UnsupportedEncodingException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition",
                "attachment;fileName=" + new String(fileName.getBytes("gbk"), "iso-8859-1"));
        try {
            String rootPath = httpServletRequest.getSession().getServletContext().getRealPath("/") + uploadFolder + "/"
                    + SecurityUtil.getCurrentUserName() + "/";
            String wholeRealPath = rootPath + folderPath + "/" + fileName;
            File file = new File(wholeRealPath);
            InputStream inputStream = new FileInputStream(file);
            OutputStream os = response.getOutputStream();
            byte[] b = new byte[1024];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/upload")
    public
    @ResponseBody
    Object singleUpload(@RequestParam(value = "file") MultipartFile multipartFile,
            @RequestParam(value = "folderPath") String folderPath, HttpServletRequest httpServletRequest)
            throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return ResponseUtil.error("请先上传附件");
        }
        String rootPath =
                httpServletRequest.getSession().getServletContext().getRealPath("/") + uploadFolder + "/" + SecurityUtil
                        .getCurrentUserName() + "/";
        String wholeRealPath = rootPath + folderPath;
        FileUtil.saveFileFromInputStream(multipartFile.getInputStream(), wholeRealPath,
                multipartFile.getOriginalFilename());
        return ResponseUtil.success();
    }

    @RequestMapping(value = "/zip", method = RequestMethod.POST)
    public
    @ResponseBody
    Object zip(HttpServletRequest httpServletRequest, @RequestParam(value = "folderPath") String folderPath,
            @RequestParam(value = "name") String name, @RequestParam(value = "zipName") String zipName) {
        String rootPath =
                httpServletRequest.getSession().getServletContext().getRealPath("/") + uploadFolder + "/" + SecurityUtil
                        .getCurrentUserName() + "/";
        String wholeRealPath = rootPath + folderPath;
        String[] fileOrFolderNames = name.split(",");
        String zipPath = wholeRealPath + "/" + zipName + ".zip";
        if (fileOrFolderNames.length == 1) {
            CompressUtil.compress(zipPath, wholeRealPath + "/" + name);
        } else {
            List<String> list = Arrays.asList(fileOrFolderNames);
            List<String> pathList = new ArrayList<>();
            for (String fileOrFolderName : list) {
                fileOrFolderName = wholeRealPath + "/" + fileOrFolderName;
                pathList.add(fileOrFolderName);
            }
            CompressUtil.compress(zipPath, pathList);
        }
        return ResponseUtil.success();
    }

    @RequestMapping(value = "/unCompress", method = RequestMethod.POST)
    public
    @ResponseBody
    Object unCompress(HttpServletRequest httpServletRequest, @RequestParam(value = "folderPath") String folderPath,
            @RequestParam(value = "name") String name) throws Exception {
        String rootPath =
                httpServletRequest.getSession().getServletContext().getRealPath("/") + uploadFolder + "/" + SecurityUtil
                        .getCurrentUserName() + "/";
        String wholeRealPath = rootPath + folderPath;
        if (name.toLowerCase().endsWith(".rar")) {
            CompressUtil.unRarFile(wholeRealPath + "/" + name, wholeRealPath);
        } else if (name.toLowerCase().endsWith(".zip")) {
            CompressUtil.unZipFiles(wholeRealPath + "/" + name, wholeRealPath);
        } else {
            ResponseUtil.error("文件格式不正确！");
        }
        return ResponseUtil.success();
    }

    @SuppressWarnings("rawtypes")
    class NameComparator implements Comparator {

        public int compare(Object a, Object b) {
            Hashtable hashA = (Hashtable) a;
            Hashtable hashB = (Hashtable) b;
            if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
                return 1;
            } else {
                return ((String) hashA.get("filename")).compareTo((String) hashB.get("filename"));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    class SizeComparator implements Comparator {

        public int compare(Object a, Object b) {
            Hashtable hashA = (Hashtable) a;
            Hashtable hashB = (Hashtable) b;
            if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
                return 1;
            } else {
                if (((Long) hashA.get("filesize")) > ((Long) hashB.get("filesize"))) {
                    return 1;
                } else if (((Long) hashA.get("filesize")) < ((Long) hashB.get("filesize"))) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    class TypeComparator implements Comparator {

        public int compare(Object a, Object b) {
            Hashtable hashA = (Hashtable) a;
            Hashtable hashB = (Hashtable) b;
            if (((Boolean) hashA.get("is_dir")) && !((Boolean) hashB.get("is_dir"))) {
                return -1;
            } else if (!((Boolean) hashA.get("is_dir")) && ((Boolean) hashB.get("is_dir"))) {
                return 1;
            } else {
                return ((String) hashA.get("filetype")).compareTo((String) hashB.get("filetype"));
            }
        }
    }

}
