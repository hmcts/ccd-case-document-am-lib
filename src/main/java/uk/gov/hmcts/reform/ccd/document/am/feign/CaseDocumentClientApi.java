package uk.gov.hmcts.reform.ccd.document.am.feign;

import feign.form.spring.SpringFormEncoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import uk.gov.hmcts.reform.ccd.document.am.model.Document;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentTTLRequest;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentTTLResponse;
import uk.gov.hmcts.reform.ccd.document.am.model.DocumentUploadRequest;
import uk.gov.hmcts.reform.ccd.document.am.model.UploadResponse;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@FeignClient(name = "case-document-am-client-api", url = "${case_document_am.url}/cases/documents")
public interface CaseDocumentClientApi {

    String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    String DOCUMENT_ID = "documentId";

    @PostMapping(produces = APPLICATION_JSON_VALUE,  consumes = MULTIPART_FORM_DATA_VALUE)
    UploadResponse uploadDocuments(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                   @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuth,
                                   @RequestBody DocumentUploadRequest uploadRequest);

    @GetMapping(value = "/{documentId}/binary")
    ResponseEntity<Resource> getDocumentBinary(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                               @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuth,
                                               @PathVariable(DOCUMENT_ID) UUID documentId);

    @GetMapping(value = "/{documentId}")
    Document getMetadataForDocument(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                    @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuth,
                                    @PathVariable(DOCUMENT_ID) UUID documentId);

    @DeleteMapping(value = "/{documentId}")
    void deleteDocument(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                  @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuth,
                                  @PathVariable(DOCUMENT_ID) UUID documentId,
                                  @RequestParam("permanent") boolean permanent);

    @PatchMapping(value = "/{documentId}")
    DocumentTTLResponse patchDocument(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                      @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuth,
                                      @PathVariable(DOCUMENT_ID) UUID documentId,
                                      @RequestBody DocumentTTLRequest ttl);

    @Configuration
    @EnableFeignClients
    class MultipartSupportConfig {

        @Autowired
        private ObjectFactory<HttpMessageConverters> messageConverters;

        @Bean
        @Primary
        @Scope("prototype")
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder(new SpringEncoder(messageConverters));
        }
    }
}
