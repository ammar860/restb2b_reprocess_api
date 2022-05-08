package com.restb2b.reprocess.api.config;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.beans.BeanProperty;

@ApplicationScoped
public class BlobConfig {
    @ConfigProperty(name = "azure.myblob.url")
    private String url;

    @ConfigProperty(name = "azure.storage.container-name")
    private String container;

    @Produces
    public BlobServiceClient getBlobServiceClient() {
//        StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
//        String endpoint = String.format(Locale.ROOT, url, accountName);
        return new BlobServiceClientBuilder().connectionString(url).buildClient();
    }

    @Produces
    public BlobContainerClient getBlobContainerClient(BlobServiceClient getBlobServiceClient) {
        return getBlobServiceClient.getBlobContainerClient(container);
    }

}
