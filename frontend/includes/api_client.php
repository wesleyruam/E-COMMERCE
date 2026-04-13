<?php
/**
 * Cliente HTTP para comunicação com as APIs REST
 * Não acessível diretamente — use via includes
 */

if (!defined('APP_RUNNING')) {
    http_response_code(403);
    exit('Acesso negado.');
}

/**
 * Realiza uma requisição HTTP via cURL
 *
 * @param string      $url     URL completa
 * @param string      $method  GET | POST | PUT | DELETE | PATCH
 * @param array|null  $body    Dados a enviar como JSON
 * @param string|null $token   JWT token para Authorization header
 * @return array{status: int, body: mixed}
 */
function apiRequest(string $url, string $method = 'GET', ?array $body = null, ?string $token = null): array
{
    $ch = curl_init($url);

    $headers = ['Content-Type: application/json', 'Accept: application/json'];

    if ($token) {
        $headers[] = 'Authorization: Bearer ' . $token;
    }

    curl_setopt_array($ch, [
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_TIMEOUT        => 10,
        CURLOPT_CUSTOMREQUEST  => strtoupper($method),
        CURLOPT_HTTPHEADER     => $headers,
        CURLOPT_SSL_VERIFYPEER => false, // true em produção com cert válido
    ]);

    if ($body !== null) {
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($body));
    }

    $response   = curl_exec($ch);
    $statusCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $error      = curl_error($ch);
    curl_close($ch);

    if ($error) {
        return ['status' => 0, 'body' => null, 'error' => $error];
    }

    return [
        'status' => $statusCode,
        'body'   => json_decode($response, true),
    ];
}

// --- Atalhos semânticos ---

function apiGet(string $url, ?string $token = null): array
{
    return apiRequest($url, 'GET', null, $token);
}

function apiPost(string $url, ?array $body = null, ?string $token = null): array
{
    return apiRequest($url, 'POST', $body, $token);
}

function apiPut(string $url, array $body, ?string $token = null): array
{
    return apiRequest($url, 'PUT', $body, $token);
}

function apiDelete(string $url, ?string $token = null): array
{
    return apiRequest($url, 'DELETE', null, $token);
}

function apiPatch(string $url, array $body, ?string $token = null): array
{
    return apiRequest($url, 'PATCH', $body, $token);
}

/**
 * Retorna true se a resposta indica sucesso (2xx)
 */
function isApiSuccess(array $response): bool
{
    return isset($response['status']) && $response['status'] >= 200 && $response['status'] < 300;
}

function apiErrorMessage(array $response, string $fallback): string
{
    return $response['body']['message'] ?? $response['error'] ?? $fallback;
}

function apiData(array $response): mixed
{
    return $response['body']['data'] ?? null;
}

function fetchAuthenticatedUser(?string $token): ?array
{
    if (!$token) {
        return null;
    }

    $response = apiGet(API_USERS, $token);
    $data     = apiData($response);

    return isApiSuccess($response) && is_array($data) ? $data : null;
}
