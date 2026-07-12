CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS tournaments(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    discipline VARCHAR(255) NOT NULL,
    min_participants INT NOT NULL,
    max_participants INT NOT NULL,
    status VARCHAR(50) NOT NULL
);
CREATE TABLE tournament_users (
tournament_id BIGINT NOT NULL,
user_id BIGINT NOT NULL,
PRIMARY KEY (tournament_id, user_id),
FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);