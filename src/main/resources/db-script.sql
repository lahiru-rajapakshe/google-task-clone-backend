
CREATE TABLE IF NOT EXISTS user
(
    id          CHAR(36) PRIMARY KEY,
    email       VARCHAR(100) UNIQUE NOT NULL,
    password    VARCHAR(100)        NOT NULL,
    full_name   VARCHAR(200)        NOT NULL,
    profile_pic VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS  task_list
(
    id      INT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(200) NOT NULL,
    user_id CHAR(36)     NOT NULL,
    CONSTRAINT FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS  task
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(200)                      NOT NULL,
    details      VARCHAR(500),
    position     INT                                        DEFAULT 0,
    status       ENUM ('COMPLETED', 'needsAction') NOT NULL DEFAULT 'needsAction',
    task_list_id INT                               NOT NULL,
    CONSTRAINT FOREIGN KEY fk_task (task_list_id) REFERENCES task_list (id)
);

CREATE TABLE IF NOT EXISTS  sub_task
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    title    VARCHAR(200)                      NOT NULL,
    details  VARCHAR(500),
    position INT                                        DEFAULT 0,
    status   ENUM ('completed', 'needsAction') NOT NULL DEFAULT 'needsAction',
    task_id  INT                               NOT NULL,
    CONSTRAINT FOREIGN KEY fk_task (task_id) REFERENCES task (id)
);