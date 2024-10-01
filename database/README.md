# Setup the PostgreSQL Database

## 1. Connect to the Database Server (PostgreSQL)

```
psql -h <hostname> -p <port> -U postgres
```

## 2. Create the database
```
CREATE DATABASE mydatabase;
```

## 3. List all the databases
```
\l
```

## 4. Create DB User with Password
```
CREATE USER myuser WITH PASSWORD 'mypassword';
```

## 5. Grant Access to the Database
```
GRANT ALL PRIVILEGES ON DATABASE mydatabase TO myuser;
```
## 6. Change Database Ownership
```
ALTER DATABASE mydatabase OWNER TO myuser;
```

## 7. Quit the DB command line  prompt
```
\q
```


