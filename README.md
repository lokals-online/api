# Lokal API Technical Documentation
### 1. Introduction
   The Local API is a web service that provides functionality for users to create and manage their own lokal game rooms. 
   The API allows users to create and customize their own game rooms, manage tables and games within the rooms, and connect with other players to play games together.

### 2. API Endpoints
   The following endpoints are available in the Local API:

#### User
/register: Allows users to create a new account by providing a username and password.
/login: Allows users to authenticate and access their account by providing a username and password.
#### Lokal
PUT /lokal/{lokalId}: Allows users to update their lokal settings by providing a lokal name.
POST /lokal: Allows users to create a new lokal by providing a lokal name.
DELETE /lokals/{lokalId}: Allows users to delete a lokal by providing a lokal ID.
#### Table
POST /lokals/{lokalId}/tables: Allows users to create a new table within a lokal by providing a table name.
DELETE /lokals/{lokalId}/tables/{tableId}: Allows users to delete a table within a lokal by providing a lokal ID and table ID.
#### Games
POST /lokals/{lokalId}/tables/{tableId}/games: Allows users to create a new game within a table by providing game-specific details.
DELETE /lokals/{lokalId}/tables/{tableId}/games/{gameId}: Allows users to delete a game within a table by providing a lokal ID, table ID, and game ID.
### 3. Authentication
The Local API uses token-based authentication. Users must authenticate with the API by providing their username and password through the /login endpoint. Upon successful authentication, the API will return an access token which can be used to make subsequent requests to the API.
