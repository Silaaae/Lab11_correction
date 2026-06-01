<?php
class Connexion {
    private $pdo;

    public function __construct() {
        $host     = 'localhost';
        $dbname   = 'localisation';
        $user     = 'root';
        $password = '';

        try {
            $dsn = "mysql:host=$host;dbname=$dbname;charset=utf8mb4";
            $this->pdo = new PDO($dsn, $user, $password);
            $this->pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch (PDOException $e) {
            die(json_encode(["status" => "error", "message" => "Connexion BDD échouée : " . $e->getMessage()]));
        }
    }

    public function getPdo() {
        return $this->pdo;
    }
}
?>
