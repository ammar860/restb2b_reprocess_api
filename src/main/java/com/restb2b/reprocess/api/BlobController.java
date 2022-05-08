package com.restb2b.reprocess.api;

import com.azure.core.http.HttpHeaders;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.restb2b.reprocess.api.constant.ApiErrorCode;
import com.restb2b.reprocess.api.constant.JsonKey;
import org.jboss.resteasy.reactive.RestQuery;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/blobs")
@Produces(MediaType.APPLICATION_JSON)
public class BlobController {

    final BlobContainerClient blobContainerClient;

    public BlobController(BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
    }


    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/upload")
    @POST
    public Response upload(@FormParam(value = "file") File file) {
        Map<Object,Object> jsonObject = new HashMap<>();
        String responseMsg;
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(file.getName());
            blobClient.upload(new FileInputStream(file),file.length());
            String blobUrl = blobClient.getBlobUrl();
            responseMsg = "Successfully Uploaded";
            jsonObject.put(JsonKey.URL,blobUrl);
            jsonObject.put(JsonKey.SUCCESS,true);
            jsonObject.put(JsonKey.MESSAGE,responseMsg);
        }
        catch(Exception e){
            responseMsg = e.getMessage();
            jsonObject.put(JsonKey.SUCCESS,false);
            jsonObject.put(JsonKey.ERROR_CODE, ApiErrorCode.UNKNOWN);
            jsonObject.put(JsonKey.MESSAGE,responseMsg);
        }
        return Response.ok(jsonObject).build();
    }

    @Path("/getAll")
    @GET
    public Response getAllBlobs(){
        Map<Object,Object> jsonObject = new HashMap<>();
        String responseMsg;
        try {
            PagedIterable<BlobItem> blobItems = blobContainerClient.listBlobs();
            List a=new ArrayList();
            for (BlobItem blobItem : blobItems) {
                a.add(blobItem.getName());
            }
            responseMsg = "Successfully Got The Data";
            jsonObject.put(JsonKey.SUCCESS,true);
            jsonObject.put(JsonKey.DATA,a);
            jsonObject.put(JsonKey.MESSAGE,responseMsg);
        }catch (Exception e){
            responseMsg = e.getMessage();
            jsonObject.put(JsonKey.SUCCESS,false);
            jsonObject.put(JsonKey.ERROR_CODE, ApiErrorCode.UNKNOWN);
            jsonObject.put(JsonKey.MESSAGE,responseMsg);
        }

        return Response.ok(jsonObject).build();
    }


    @Path("/download")
    @GET
    public Response download(@RestQuery(value = "fileName") String fileName) {
        Map<Object,Object> jsonObject = new HashMap<>();
        String responseMsg;
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            ByteArrayOutputStream outputStream1=new ByteArrayOutputStream();
            blobClient.download(outputStream1);
            responseMsg = "Successfully download";
            jsonObject.put(JsonKey.SUCCESS,true);
            jsonObject.put(JsonKey.DATA,outputStream1.toString());
            jsonObject.put(JsonKey.MESSAGE,responseMsg);

        }catch (Exception e){
            responseMsg = e.getMessage();
            jsonObject.put(JsonKey.SUCCESS,false);
            jsonObject.put(JsonKey.ERROR_CODE, ApiErrorCode.UNKNOWN);
            jsonObject.put(JsonKey.MESSAGE,responseMsg);
        }

        return Response.ok(jsonObject).build();
    }
}