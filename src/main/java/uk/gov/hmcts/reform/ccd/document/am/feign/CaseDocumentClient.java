package uk.gov.hmcts.reform.ccd.document.am.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.ccd.document.am.model.Document;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentTTLRequest;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentTTLResponse;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentUploadRequest;
import uk.gov.hmcts.reform.ccd.document.am.model.Classification;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Service
public class CaseDocumentClient {

    private CaseDocumentClientApi caseDocumentClientApi;

    @Autowired
    public CaseDocumentClient(CaseDocumentClientApi caseDocumentClientApi) {
        this.caseDocumentClientApi = caseDocumentClientApi;
    }

    public UploadResponse uploadDocuments(String authorisation, String serviceAuth, String caseTypeId,
                                          String jurisdictionId, List<MultipartFile> files) {
        return uploadDocuments(authorisation, serviceAuth, caseTypeId, jurisdictionId, files,
                               Classification.RESTRICTED);
    }

    public UploadResponse uploadDocuments(String authorisation, String serviceAuth,
                                           String caseTypeId,
                                           String jurisdictionId,
                                           List<MultipartFile> files,
                                           Classification classification) {

        DocumentUploadRequest documentUploadRequest = new DocumentUploadRequest(classification.toString(),
                                                                                caseTypeId,
                                                                                jurisdictionId,
                                                                                files);

        return caseDocumentClientApi.uploadDocuments(authorisation, serviceAuth, documentUploadRequest);
    }

    public ResponseEntity<Resource> getDocumentBinary(String authorisation, String serviceAuth, UUID documentId) {
        return caseDocumentClientApi.getDocumentBinary(authorisation, serviceAuth, documentId);
    }

    public Document getMetadataForDocument(String authorisation, String serviceAuth, UUID documentId) {
        return caseDocumentClientApi.getMetadataForDocument(authorisation, serviceAuth, documentId);
    }

    public void deleteDocument(String authorisation, String serviceAuth, UUID documentId, boolean permanent) {
        caseDocumentClientApi.deleteDocument(authorisation, serviceAuth, documentId, permanent);
    }

    public DocumentTTLResponse patchDocument(String authorisation, String serviceAuth, UUID documentId,
                                             DocumentTTLRequest ttl) {
        return caseDocumentClientApi.patchDocument(authorisation, serviceAuth, documentId, ttl);
    }

}