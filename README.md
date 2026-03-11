# 🎵 Music Social Platform – Rate, Share & Discover Music  
> A CS 309 Project by Team `MK_1_8`

## 📘 Project Description

**Music Social Platform** is a collaborative music discovery and rating application that allows users to rate songs, create group playlists, chat with friends, and discover new music through integration with the Deezer API. Think **Spotify meets social media** – where music becomes a shared experience.

The app includes:
- Song rating system (1-5 stars) with real-time updates via WebSockets
- Friend management and private messaging
- Group playlist creation and collaboration
- Music trivia games
- Profile customization
- Deezer API integration for song discovery

---

## 🚀 Features

### For All Users
- ✅ Rate songs and see real-time average ratings update
- ✅ Search for songs by title or artist
- ✅ Add songs to personal favorites
- ✅ View top-rated and randomly selected songs
- ✅ Create and join group playlists with friends
- ✅ Real-time chat with friends
- ✅ Play music trivia games (from OpenTDB API)
- ✅ Customize profile with bio and background color

### Real-Time Features (WebSockets)
- ✅ Live rating updates when users rate songs
- ✅ Instant messaging between friends
- ✅ Real-time song list updates for all connected users

### External API Integrations
- ✅ **Deezer API** – Fetch songs, charts, and random tracks
- ✅ **OpenTDB API** – Generate music trivia questions

---

## 🎥 Demo

[Watch demo on YouTube](https://www.youtube.com/watch?v=tyGnnJtLe8M&list=PL6BdlkdKLEB9U0F4VMXt6Ck7DX6TAdupE&index=37)

[![Watch demo on YouTube](https://i.ytimg.com/vi/tyGnnJtLe8M/hqdefault.jpg?sqp=-oaymwFACKgBEF5IWvKriqkDMwgBFQAAiEIYAdgBAeIBCggYEAIYBjgBQAHwAQH4Af4JgALQBYoCDAgAEAEYQyBKKGUwDw==&rs=AOn4CLCH9HOxJYMmU9ZUnlqpZKMAhB866g)](https://www.youtube.com/watch?v=tyGnnJtLe8M&list=PL6BdlkdKLEB9U0F4VMXt6Ck7DX6TAdupE&index=37)

---

## 🧠 Architecture

### Backend Components

| Controller | Purpose | Key Endpoints |
|------------|---------|---------------|
| **SongController** | Manage songs, Deezer integration | `/songs`, `/songs/fetch-charts`, `/songs/search` |
| **RatingController** | Handle song ratings | `/ratings/rate`, `/ratings/{songId}/average` |
| **SongSocket** | Real-time rating updates | WebSocket: `/rate/{email}` |
| **RatingSocket** | Per-song rating WebSocket | WebSocket: `/rate/{email}/{songId}` |
| **ChatSocket** | Real-time messaging | WebSocket: `/chat/{email}/{friendEmail}` |
| **FriendController** | Manage friendships | `/users/{userId}/addFriend`, `/users/{userId}/friends` |
| **GroupPlaylistController** | Collaborative playlists | `/users/{userId}/playlists`, `/playlists/{id}/songs` |
| **ProfileController** | User profiles | `/users/{userId}/profile/bio`, `/users/{userId}/profile/color` |
| **FavoritesController** | Personal song collections | `/users/{userId}/favorites` |
| **TriviaController** | Music trivia games | `/trivia/generate`, `/trivia/{id}/submit` |

### Database Entities
- **User** – User accounts and authentication
- **Song** – Music tracks with ratings
- **Rating** – User ratings for songs
- **Friend** – Friend relationships between users
- **Message** – Chat messages between friends
- **GroupPlaylist** – Collaborative playlists with multiple users
- **Favorites** – User's favorite songs collection
- **Trivia** – Generated trivia games with questions and answers
- **Profile** – User profiles with bio and customization

---

## 🛠 Tech Stack

| Layer | Tech Used |
|-------|-----------|
| **Backend Framework** | Spring Boot (Java) |
| **Real-Time Communication** | WebSockets (Jakarta EE) |
| **Database** | JPA / Hibernate (configurable) |
| **API Documentation** | Swagger / OpenAPI 3 |
| **External APIs** | Deezer API, OpenTDB API |
| **Build Tool** | Maven |
| **Version Control** | Git & GitLab |

---

## 👤 User Flow

1. **User signs up** and creates a profile with bio and color preference
2. **Discovers music** through:
   - Deezer charts (fetched hourly)
   - Random song suggestions
   - Search by title/artist
3. **Rates songs** (1-5 stars) – ratings update in real-time for all users
4. **Adds friends** by email – mutual acceptance required
5. **Creates group playlists** with friends and collaboratively adds songs
6. **Chats with friends** in real-time
7. **Plays trivia games** – 5 music questions per game
8. **Saves favorites** for quick access to preferred songs

---

## 📊 API Endpoints Summary

### Songs
- `GET /songs` – Get all songs
- `GET /songs/top-rated` – Get top-rated songs
- `GET /songs/random` – Get random songs
- `GET /songs/search?query=` – Search songs
- `POST /songs` – Create new song
- `POST /songs/fetch-random` – Fetch random songs from Deezer
- `POST /songs/fetch-charts` – Fetch Deezer charts
- `POST /songs/fetch-from-deezer/{deezerTrackId}` – Fetch specific song from Deezer
- `GET /songs/fetch/{deezerTrackId}` – Get song by Deezer ID
- `DELETE /songs/{songId}` – Delete song

### Ratings
- `POST /ratings/rate?songId=&userEmail=&rating=` – Rate a song
- `GET /ratings/{songId}/average` – Get average rating

### Friends
- `POST /users/{userId}/addFriend` – Send friend request
- `GET /users/{userId}/friends` – Get friends list
- `PUT /users/{userId}/friends/email/{friendEmail}` – Update friend
- `DELETE /users/{userId}/friends/{friendEmail}` – Remove friend

### Playlists
- `POST /users/{userId}/playlists` – Create playlist
- `GET /users/{userId}/playlists/myPlaylists` – Get user's playlists
- `PUT /users/{userId}/playlists/{playlistId}/updateName` – Update playlist name
- `POST /users/{userId}/playlists/{playlistId}/addUser` – Add user to playlist
- `GET /playlists` – Get all playlists
- `GET /users/{userId}/playlists/{playlistId}/users` – Get users in playlist
- `GET /users/{userId}/playlists/{playlistId}/songs` – Get songs in playlist
- `GET /users/{userId}/playlists/{playlistId}` – Get playlist details
- `POST /users/{userId}/playlists/{playlistId}/songs/{songId}` – Add song to playlist
- `DELETE /users/{userId}/playlists/{playlistId}/songs/{songId}` – Remove song from playlist
- `DELETE /users/{userId}/playlists/{playlistId}/remove` – Leave playlist

### Profile
- `GET /profiles/users/{userId}/friendCount` – Get friend count
- `GET /users/{userId}/profile/bio` – Get bio
- `POST /users/{userId}/profile/bio` – Create bio
- `PUT /users/{userId}/profile/bio` – Update bio
- `DELETE /users/{userId}/profile/bio` – Delete bio
- `GET /users/{userId}/profile/color` – Get background color
- `PUT /users/{userId}/profile/color` – Update background color

### Favorites
- `GET /users/{userId}/favorites` – Get favorites
- `POST /users/{userId}/favorites/songs/{songId}` – Add to favorites
- `DELETE /users/{userId}/favorites/songs/{songId}` – Remove from favorites

### Trivia
- `POST /trivia/generate` – Generate trivia game
- `PUT /trivia/{id}/submit` – Submit answers
- `GET /trivia/{id}` – Get trivia by ID
- `GET /trivia/fetch` – Get all trivia
- `DELETE /trivia/{id}` – Delete trivia

### Messages
- `GET /messages?email=&friendEmail=` – Get chat history between users

---

## 🔌 WebSocket Connections

| Endpoint | Purpose | Message Format |
|----------|---------|----------------|
| `ws://server/rate/{email}` | Receive real-time song rating updates | Server sends: `{"id":1,"title":"Song","artist":"Artist","rating":4.5,"cover":"url"}` |
| `ws://server/rate/{email}/{songId}` | Get updates for specific song | Client sends: `{"rating":5}`<br>Server sends rating updates |
| `ws://server/chat/{email}/{friendEmail}` | Real-time messaging between friends | Client sends plain text<br>Server sends: `sender@email.com [HH:mm:ss]: message` |

---

## 🧪 Testing the APIs

Base URL: `http://localhost:8080`

### Example Requests

```bash
# Get all songs
curl http://localhost:8080/songs

# Rate a song
curl -X POST "http://localhost:8080/ratings/rate?songId=1&userEmail=test@example.com&rating=5"

# Fetch Deezer charts
curl -X POST http://localhost:8080/songs/fetch-charts

# Search for songs
curl "http://localhost:8080/songs/search?query=love"

# Create a playlist
curl -X POST http://localhost:8080/users/1/playlists \
  -H "Content-Type: application/json" \
  -d '{"name":"My Playlist","users":["friend@example.com"]}'

# Generate trivia
curl -X POST http://localhost:8080/trivia/generate

# Submit trivia answers
curl -X PUT http://localhost:8080/trivia/1/submit \
  -H "Content-Type: application/json" \
  -d '["Answer 1","Answer 2","Answer 3","Answer 4","Answer 5"]'

# Add song to favorites
curl -X POST http://localhost:8080/users/1/favorites/songs/1

# Update profile color
curl -X PUT http://localhost:8080/users/1/profile/color \
  -H "Content-Type: application/json" \
  -d '{"backgroundColor":"FF5733"}'

# Send friend request
curl -X POST http://localhost:8080/users/1/addFriend \
  -H "Content-Type: application/json" \
  -d '{"friendEmail":"friend@example.com"}'

# Get chat history
curl "http://localhost:8080/messages?email=user@example.com&friendEmail=friend@example.com"