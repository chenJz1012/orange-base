package com.orange.base.core.service;

import com.orange.base.basedao.service.IService;
import com.orange.base.core.model.Attachment;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by chenguojun on 8/28/16.
 */
public interface IAttachmentService extends IService<Attachment> {

    Attachment uploadFileAttachement(HttpServletRequest request, MultipartFile file, String dirName, long maxSize,
            HashMap<String, String> extLimitMap,Integer suffix) throws IOException;
}
