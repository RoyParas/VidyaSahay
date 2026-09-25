package com.vidyasahay.vidyasahay.service;

import java.util.List;

import com.vidyasahay.vidyasahay.dto.request.GetDocumentListRequest;
import com.vidyasahay.vidyasahay.dto.response.DocumentListResponse;
import com.vidyasahay.vidyasahay.dto.response.DocumentTypeResponse;

public interface DocumentService {
	  List<DocumentListResponse> getDocumentListById(GetDocumentListRequest request);

    List<DocumentTypeResponse> getAllDocumentTypes();	
}
