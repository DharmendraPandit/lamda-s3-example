package example;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandlerForS3Lamda implements RequestHandler<S3Event, String> {
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    private final String XML_TYPE = "XML";
    private final String HTML_TYPE = "HTML";
    //private final String DEST_BUCKET_NAME = "s3bucket-dharmendra";
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        logger.info("###HandlerForS3Lamda EVENT: " + gson.toJson(s3Event));
        String destinationKey = "other/";
        S3EventNotification.S3EventNotificationRecord record = s3Event.getRecords().get(0);
        String bucketName = record.getS3().getBucket().getName();
        String sourceKey = record.getS3().getObject().getUrlDecodedKey();
        logger.info("###bucketName: " + bucketName + ", sourceKey: "+ sourceKey);
        Matcher matcher = Pattern.compile(".*\\.([^\\.]*)").matcher(sourceKey);
        if (!matcher.matches()) {
            logger.info("Unable to infer file type for key " + sourceKey);
            return "";
        }
        logger.info("matcher.group(0): " + matcher.group(0));
        logger.info("matcher.group(1): " + matcher.group(1));
        String fileType = matcher.group(1);
        if (XML_TYPE.equalsIgnoreCase(fileType)) {
            destinationKey = "xml/";
        } else if (HTML_TYPE.equalsIgnoreCase(fileType)) {
            destinationKey = "html/";
        }
        destinationKey += sourceKey;

        try {
            logger.info("initiated copied from " + bucketName + " to " + bucketName);
            AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
            CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketName, sourceKey, bucketName, destinationKey);
            s3Client.copyObject(copyObjRequest);
            logger.info("successfully copied from " + bucketName + " to " + bucketName);
        } catch (AmazonServiceException e) {
            logger.error("AmazonServiceException: " + e.getErrorMessage());
        } catch (SdkClientException e) {
            logger.error("SdkClientException: " + e.getMessage());
        }

        return "Ok";
    }
}
