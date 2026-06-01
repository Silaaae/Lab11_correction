<?php
header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER["REQUEST_METHOD"] !== "POST") {
    echo json_encode(["status" => "error", "message" => "Méthode non autorisée"]);
    exit;
}

include_once 'service/PositionService.php';

$latitude     = $_POST['latitude']     ?? null;
$longitude    = $_POST['longitude']    ?? null;
$datePosition = $_POST['date_position'] ?? null;
$imei         = $_POST['imei']         ?? null;

if (!$latitude || !$longitude) {
    echo json_encode(["status" => "error", "message" => "Coordonnées manquantes"]);
    exit;
}

try {
    $service  = new PositionService();
    $position = new Position(null, $latitude, $longitude, $datePosition, $imei);
    $service->create($position);
    echo json_encode(["status" => "success", "message" => "Position enregistrée"]);
} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>
