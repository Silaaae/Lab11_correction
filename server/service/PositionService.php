<?php
include_once __DIR__ . '/../dao/IDao.php';
include_once __DIR__ . '/../classe/Position.php';
include_once __DIR__ . '/../connexion/Connexion.php';

class PositionService implements IDao {
    private $pdo;

    public function __construct() {
        $connexion = new Connexion();
        $this->pdo = $connexion->getPdo();
    }

    public function create($position) {
        $sql = "INSERT INTO `position` (latitude, longitude, date_position, imei)
                VALUES (:latitude, :longitude, :date_position, :imei)";

        $stmt = $this->pdo->prepare($sql);
        $stmt->execute([
            ':latitude'      => $position->getLatitude(),
            ':longitude'     => $position->getLongitude(),
            ':date_position' => $position->getDatePosition(),
            ':imei'          => $position->getImei(),
        ]);
    }

    public function update($obj) { /* non implémenté */ }
    public function delete($obj) { /* non implémenté */ }
    public function getById($id) { /* non implémenté */ }
    public function getAll()     { /* non implémenté */ }
}
?>
