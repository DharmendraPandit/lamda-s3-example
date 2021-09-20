


If you use the AWS CLI v2, add the following to your [configuration file](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html) (`~/.aws/config`):

```
cli_binary_format=raw-in-base64-out
```

This setting enables the AWS CLI v2 to load JSON events from a file, matching the v1 behavior.

To create a new bucket for deployment artifacts, run `1-create-bucket.sh`.

    lamda-s3-example$ ./1-create-bucket.sh
    make_bucket: lambda-artifacts-a5e491dbb5b22e0d

# Deploy
To deploy the application, run `3-deploy.sh`.

    lamda-s3-example$ ./3-deploy.sh
    BUILD SUCCESSFUL in 1s
    Successfully packaged artifacts and wrote output template to file out.yml.
    Waiting for changeset to be created..
    Successfully created/updated stack - s3-java

This script uses AWS CloudFormation to deploy the Lambda functions and an IAM role. If the AWS CloudFormation stack that contains the resources already exists, the script updates it with any changes to the template or function code.

You can also build the application with Maven. To use maven, add `mvn` to the command.

    lamda-s3-example$ ./3-deploy.sh mvn
    [INFO] Scanning for projects...
    [INFO] -----------------------< com.example:s3-java >-----------------------
    [INFO] Building s3-java-function 1.0-SNAPSHOT
    [INFO] --------------------------------[ jar ]---------------------------------
    ...

# Test
To upload an image file to the application bucket and trigger the function, run `4-upload.sh`.

    lamda-s3-example$ ./4-upload.sh

To invoke the function directly, run `5-invoke.sh`.

    lamda-s3-example$ ./5-invoke.sh
    {
        "StatusCode": 200,
        "ExecutedVersion": "$LATEST"
    }

Let the script invoke the function a few times and then press `CRTL+C` to exit.

The application uses AWS X-Ray to trace requests. Open the [X-Ray console](https://console.aws.amazon.com/xray/home#/service-map) to view the service map.

# Cleanup
To delete the application, run `6-cleanup.sh`.

    lamda-s3-example$ ./6-cleanup.sh
--------------------------------------------------
Description: trigger an event from s3 when file is copied and base on file it place in the respective folder
