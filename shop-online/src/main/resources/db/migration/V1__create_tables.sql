
CREATE TABLE users (
 id BIGSERIAL PRIMARY KEY,
 name VARCHAR(100) NOT NULL,
 email VARCHAR(150) NOT NULL UNIQUE,
 password VARCHAR(255) NOT NULL,
 role VARCHAR(20) NOT NULL DEFAULT 'CLIENT'
);
CREATE TABLE categories (
 id BIGSERIAL PRIMARY KEY,
 name VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE products (
 id BIGSERIAL PRIMARY KEY,
 name VARCHAR(150) NOT NULL,
 description TEXT,
 price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
 stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
 image_url VARCHAR(500),
 category_id BIGINT REFERENCES categories(id)
);
CREATE TABLE orders (
 id BIGSERIAL PRIMARY KEY,
 user_id BIGINT NOT NULL REFERENCES users(id),
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 status VARCHAR(30) NOT NULL DEFAULT 'PLASATA',
 total NUMERIC(10,2) NOT NULL
);
CREATE TABLE order_items (
 id BIGSERIAL PRIMARY KEY,
 order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
 product_id BIGINT NOT NULL REFERENCES products(id),
 quantity INTEGER NOT NULL CHECK (quantity > 0),
 unit_price NUMERIC(10,2) NOT NULL
);
CREATE TABLE cart_items (
 id BIGSERIAL PRIMARY KEY,
 user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
 product_id BIGINT NOT NULL REFERENCES products(id),
 quantity INTEGER NOT NULL CHECK (quantity > 0),
 UNIQUE(user_id, product_id)
);
INSERT INTO categories(name) VALUES ('Electronice'), ('Accesorii'), ('Casa si gradina');
INSERT INTO products(name, description, price, stock, image_url, category_id) VALUES
('Casti wireless', 'Casti Bluetooth pentru utilizare zilnica.', 149.99, 20, 'https://images.unsplash.com/photo-1509978778156-518eea30166b?auto=format&fit=crop&w=700&q=80', 1),
('Mouse optic', 'Mouse USB ergonomic, precizie ridicata.', 59.90, 35, 'https://images.unsplash.com/photo-1527814050087-3793815479db?auto=format&fit=crop&w=700&q=80', 2),
('Boxa portabila', 'Boxa compacta cu conectivitate Bluetooth.', 199.00, 12, 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=700&q=80', 1),
('Lampa LED', 'Lampa moderna pentru birou.', 89.50, 18, 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=700&q=80', 3);
