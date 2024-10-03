# Setup the PostgreSQL Database

## 1. Setup using Script

### 1.1 Create the Database 
If you want to change the db port number and db user then edit the script createdb.sql

```
$ createdb
```

### 1.2 Create Tables, Indexes and Import Master and Tx Data
```
$ importdb
```

## 2. Setup the DB Manually


### 2.1. Connect to the Database Server (PostgreSQL)

```
psql -h <hostname> -p <port> -U postgres
```

### 2.2. Create the database
```
CREATE DATABASE mydatabase;
```

### 2.3. List all the databases
```
\l
```

### 2.4. Create DB User with Password
```
CREATE USER myuser WITH PASSWORD 'mypassword';
```

### 2.5. Grant Access to the Database
```
GRANT ALL PRIVILEGES ON DATABASE mydatabase TO myuser;
```
### 2.6. Change Database Ownership
```
ALTER DATABASE mydatabase OWNER TO myuser;
```

### 2.7. Quit the DB command line  prompt
```
\q
```

### 2.8 Create Tables, Indexes and Import Master and Tx Data
```
$ importdb
```
