# Shop Online – Proiect PAW

Aplicație demonstrativă de magazin online realizată cu Java 21, Spring Boot, Spring MVC, Thymeleaf, Spring Security, PostgreSQL 17, Maven și Flyway.

## Funcționalități
- catalog de produse și căutare după denumire;
- pagină de detalii pentru fiecare produs;
- înregistrare și autentificare cu parole BCrypt;
- roluri CLIENT și ADMIN;
- coș de cumpărături și plasare comandă;
- scăderea stocului la plasarea comenzii;
- istoric comenzi pentru client;
- panou de administrare pentru listarea/ștergerea produselor, adăugarea produselor și vizualizarea comenzilor;
- API JSON: `GET /api/v1/products` și `GET /api/v1/products/{id}`.

## Cerințe
- JDK 21;
- IntelliJ IDEA;
- Docker Desktop (pentru PostgreSQL);
- Maven (sau Maven Wrapper generat de IntelliJ).

## Pornire
1. Deschide Docker Desktop.
2. În terminal, din directorul proiectului, execută:
   ```bash
   docker compose up -d
   ```
3. Deschide proiectul `pom.xml` în IntelliJ IDEA și așteaptă descărcarea dependențelor Maven.
4. Rulează clasa `ro.shoponline.ShopOnlineApplication`.
5. Accesează `http://localhost:8080`.

Flyway creează automat tabelele și inserează produse demonstrative la prima pornire.

## Crearea contului de administrator
1. Înregistrează un cont normal din pagina `/register`.
2. În pgAdmin sau în terminalul PostgreSQL execută:
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE email = 'adresa-ta@email.ro';
   ```
3. Deconectează-te și autentifică-te din nou. Linkul Administrare va permite accesul la `/admin`.

## Date PostgreSQL
- Bază de date: `shop_online`
- Utilizator: `shop`
- Parolă: `shop123`
- Port: `5432`

Aceste date sunt strict demonstrative. Pentru o aplicație reală, parolele și secretele trebuie mutate în variabile de mediu.

## Structura
- `model/` – entitățile bazei de date;
- `repository/` – accesul la PostgreSQL prin Spring Data JPA;
- `service/` – logica pentru coș și comenzi;
- `web/` – controllerele MVC și API;
- `security/` – autentificare și reguli de acces;
- `templates/` – pagini Thymeleaf;
- `static/css/` – stilurile interfeței;
- `db/migration/` – scripturi Flyway.

## Observație pentru prezentare
Aplicația este un prototip didactic. Plata este simulată: plasarea comenzii înregistrează comanda în baza de date și actualizează stocul, fără integrare cu un procesator de plăți sau serviciu de livrare.
