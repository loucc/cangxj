-- Flyway V1: sys_user 初始表
CREATE TABLE IF NOT EXISTS sys_user (
    id           BIGINT PRIMARY KEY,
    username     VARCHAR(64)  NOT NULL,
    password     VARCHAR(128) NOT NULL,
    nickname     VARCHAR(64),
    email        VARCHAR(128),
    phone        VARCHAR(20),
    status       VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   VARCHAR(64),
    updated_by   VARCHAR(64),
    version      INTEGER      NOT NULL DEFAULT 0,
    deleted      SMALLINT     NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_user_username ON sys_user (username) WHERE deleted = 0;
CREATE INDEX IF NOT EXISTS idx_sys_user_status       ON sys_user (status);
CREATE INDEX IF NOT EXISTS idx_sys_user_created_at   ON sys_user (created_at DESC);

COMMENT ON TABLE  sys_user IS '系统用户';
COMMENT ON COLUMN sys_user.status  IS 'ACTIVE / DISABLED / LOCKED';
COMMENT ON COLUMN sys_user.deleted IS '逻辑删除标记，0 未删除 1 已删除';
