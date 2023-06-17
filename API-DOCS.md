# Api Endpoints
## Users
### Register
`POST /api/users/register`

Registers a new user.

Request Body:

```
{
    "username": "johnsmith",
    "password": "password123"
}
```
Responses:

* 200 OK on success
* 400 Bad Request if username or password is invalid
* 409 Conflict if username is already taken

### Login
`POST /api/users/login`

Logs in an existing user.

Request Body:

```
{
    "username": "johnsmith",
    "password": "password123"
}
```
Responses:

* 200 OK on success
* 400 Bad Request if username or password is invalid
* 401 Unauthorized if username or password is incorrect

### Lokals

#### Update Lokal Settings

`PUT /api/lokalz/:lokalId/settings`

Updates the settings of a lokal.

Request Body:

```
{
    "name": "My Lokal",
    "location": "123 Main St",
    "description": "A cozy place to hang out"
}
```
Responses:

* 200 OK on success
* 400 Bad Request if lokalId is invalid
* 401 Unauthorized if user is not logged in
* 403 Forbidden if user is not the owner of the lokal

### Table

#### Create New Table

`POST /api/lokalz/:lokalId/tables`

Creates a new table for a lokal.

Request Body:

```
{
    "name": "Table 1"
}
```
Responses:

* 200 OK on success
* 400 Bad Request if lokalId is invalid or table name is not provided
* 401 Unauthorized if user is not logged in
* 403 Forbidden if user is not the owner of the lokal

#### Close Table

`DELETE /api/lokalz/:lokalId/tables/:tableId`

Closes a table for a lokal.

Responses:

* 200 OK on success
* 400 Bad Request if lokalId or tableId is invalid
* 401 Unauthorized if user is not logged in
* 403 Forbidden if user is not the owner of the lokal or the table is not open

### Game

#### Create New Game
`POST /api/lokalz/:lokalId/tables/:tableId/games`

Creates a new game for a table in a lokal.

Request Body:

```
{
    "name": "Game 1"
}
```
Responses:

* 200 OK on success
* 400 Bad Request if lokalId or tableId is invalid or game name is not provided
* 401 Unauthorized if user is not logged in
* 403 Forbidden if user is not the owner of the lokal or the table is not open