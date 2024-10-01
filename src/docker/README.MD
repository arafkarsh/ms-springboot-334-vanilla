# Create Docker Container

## Step 1 - Verify Container Name and Org Name

1. Verify the Org Name in src/main/resources/app.props.tmpl file (service.org)
2. Verify the container name in src/main/resources/app.props.tmpl file (service.container)
3. Verify the microservice name in src/main/resources/app.props.tmpl file (service.api.name)

## Step 2 - Build the image

1. build (Build the Container)
2. scan (Scan the container for vulnerabilities)

## Step 3 - Test the image

1. start (Start the Container)
2. logs (to view the container logs) - Wait for the Container to Startup
3. Check the URL in a Browser

## Step 4 - Push the image to Cloud Repo

1. push (Push the image to Cloud)

## Other Commands

1. stop (Stop the Container)
2. stats (show container stats)
