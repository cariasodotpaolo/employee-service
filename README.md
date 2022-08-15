
PREREQUISITES:
1. Java 11 JDK
2. Maven compiler
3. Postman
4. Git Bash (Windows)

JAVA 11 INSTALLATION:
1. Download JDK 11 from
   https://adoptium.net/temurin/releases
2. Choose version 11 on the dropdown
3. Download package depending on operating system (e.g. ```.zip``` for Windows)
4. Choose the latest version update ( ``jdk-11.0.15+10`` )
5. Unzip the package to desired directory/folder
6. Add the bin subdirectory to path environment variable for direct access to Java executables/daemons.  
   EXAMPLE:  
   ``Path = D:\some\other\apps\bin;D:\OpenJDK\jdk-11.0.15+10\bin``
7. open terminal/DOS prompt/Git Bash and run ``$ java --version`` to test.  
   ``$ java --version``  
   ``openjdk 11.0.15 2022-04-19``  
   ``OpenJDK Runtime Environment Temurin-11.0.15+10 (build 11.0.15+10)``  
   ``OpenJDK 64-Bit Server VM Temurin-11.0.15+10 (build 11.0.15+10, mixed mode)``

MAVEN INSTALLATION:
1. Download Maven from https://maven.apache.org/download.cgi
2. Choose the file ``apache-maven-3.8.5-src.zip`` for Windows
3. Unzip the package to desired directory/folder
4. Add the bin subdirectory to path environment variable for direct access to Maven executables/daemons.  
   EXAMPLE:  
   ``Path = D:\some\other\apps\bin;D:\OpenJDK\jdk-11.0.15+10\bin;D:\apache-maven-3.8.5\bin``
5. Add ``JAVA_HOME`` to environment variable.  
   ``JAVA_HOME = D:\OpenJDK\jdk-11.0.15+10``
6. open terminal/DOS prompt/Git Bash and run ``$ mvn --version`` to test.  
   ``$ mvn --version``  
   ``Apache Maven 3.6.0 (97c98ec64a1fdfee7767ce5ffb20918da4f719f3; 2018-10-25T02:41:47+08:00)``  
   ``Maven home: D:\apache-maven-3.6.0``  
   ``Java version: 11.0.15, vendor: Eclipse Adoptium, runtime: D:\OpenJDK\jdk-11.0.15+10``  
   ``Default locale: en_SG, platform encoding: Cp1252``  
   ``OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"``

INSTALL GIT BASH (OR OTHER GIT CLIENTS DEPENDING ON OS):
1. Clone the employee-service repository:
   ``$ git clone https://github.com/cariasodotpaolo/employee-service.git``
2. Change Dir to repository directory and checkout master or develop branch
   ``$ cd /path/to/repo/employee-service``
   ``$ git checkout master`` or ``$ git checkout develop``
   
MAVEN BUILD:
1. Change dir/folder to the project repository root directory.  
   ``$ cd /path/to/repo/dir/employee-service``
2. Run the following command to compile and build the Java jar package:  
   ``$ mvn clean package``  //This will also run all unit tests and build will fail if at least one test failed or caused error.
   ``$ mvn clean package -DskipTests`` //skip executing tests during compile and package build
   
RUN THE APPLICATION:
1. Open terminal/ Command prompt/ Git Bash and change dir to the unzipped directory/folder.  
   ``$ cd /path/to/app/dir/employee-service``
2. NOTE: The package has pre-package Java application archive in ``target/`` subdirectory together with compiled classes
3. Run the application:  
   ``$ java -jar target/employee-service-1.0.jar``
   
API DETAILS:
CONTEXT PATH (CONTEXT ROOT): /employee-service
SERVER PORT: 8080
UPLOAD URL: http://localhost:8080/employee-service/users/upload
GET LIST URL: http://localhost:8080/employee-service/users?minSalary=0&maxSalary=5000&offset=0&limit=0
CREATE URL: POST http://localhost:8080/employee-service/users
GET URL: GET http://localhost:8080/employee-service/users/{id}
UPDATE URL: PATCH http://localhost:8080/employee-service/users/emp9001

FUNCTIONAL TESTING VIA POSTMAN:
1. Install Postman
2. Import the postman collection json file in /postman directory
3. Use the test data csv files in /postman directory
4. Feel free to create other csv files for different testing scenarios