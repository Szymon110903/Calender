# Calendar Task Board

**Calendar Task Board** to aplikacja maven do zarządzania zadaniami, która umożliwia użytkownikom dodawanie,
 przeglądanie i organizowanie zadań w kontekście wybranego dnia. Użytkownicy mogą dodawać zadania do
 odpowiednich kolumn (To Do, In Progress, Done), zmieniać status zadań oraz przeglądać je według wybranego dnia.

## Funkcjonalności

- **Rejestracja i logowanie**: Użytkownicy mogą stworzyć konto, zalogować się oraz uzyskać dostęp do
    swojego kalendarza.
- **Dodawanie zadań**: Użytkownicy mogą dodawać zadania do bazy danych oraz przypisywać
    je do odpowiednich kolumn (To Do, In Progress, Done).
- **Zarządzanie zadaniami**: Użytkownicy mogą zmieniać status zadania poprzez przeciąganie
    kart zadań pomiędzy kolumnami.
- **Kalendarz**: Zawiera funkcjonalność wyboru daty i przeglądania zadań przypisanych do wybranego dnia.


## Użyte technologie

- **Java 23** (lub wyższa)
- **JavaFX** (GUI)
- **JDBC** (do komunikacji z bazą danych)
- **BCrypt** (do szyfrowania haseł)

### Rejestracja użytkownika

1. Użytkownik wybiera opcję rejestracji.
2. Podaje nazwę użytkownika oraz hasło.
3. Hasło jest szyfrowane za pomocą biblioteki BCrypt, a dane użytkownika są zapisywane w bazie danych.

### Dodawanie zadania

1. Użytkownik wybiera datę z kalendarza.
2. Wprowadza zadanie w formularzu.
3. Zadanie jest zapisywane w bazie danych oraz wyświetlane w odpowiedniej kolumnie (To Do).

### Zmiana statusu zadania

1. Użytkownik przeciąga zadanie pomiędzy kolumnami (To Do, In Progress, Done).
2. Status zadania jest automatycznie aktualizowany w bazie danych.

## Licencja

Projekt jest udostępniony na licencji [MIT License](https://opensource.org/licenses/MIT).

---
Projekt nastawiony na rozbudowe. Jest to wczesna wersja i w miare możliwości będzie rozwijana o kolejne
funkcjonalności, poprawe wyglądu GUI, aplikacje mobilną, współdzielony serwer dla obu aplikacji. Rozwój także
jest przewidziany na system logowania zdarzeń do dziennika, własną hierarhie wyjątków i internacjonalizacje.

**Autor**: [Szymon Kaźmierczak]
**Data**: [20.04.2025]

