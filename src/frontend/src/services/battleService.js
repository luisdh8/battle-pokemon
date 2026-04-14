const API_URL = "http://localhost:8080";

// ── Batalla ──────────────────────────────────────────────────────────────────

export async function getBattle() {
  const res = await fetch(`${API_URL}/battle`);
  return res.json();
}

export async function attack(attacker, damage) {
  const res = await fetch(`${API_URL}/battle/attack`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ attacker, damage }),
  });
  return res.json();
}

// ── Lobby ─────────────────────────────────────────────────────────────────────

/** Registra el pokémon elegido en su slot. */
export async function selectPokemon(slot, name, hp) {
  const res = await fetch(`${API_URL}/battle/select`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ slot, name, hp }),
  });
  return res.ok;
}

/** Cuántos pokémon hay registrados: { registered: 0|1|2 } */
export async function getBattleStatus() {
  const res = await fetch(`${API_URL}/battle/status`);
  return res.json();
}

/** Limpia ambos slots en Firestore. */
export async function resetBattle() {
  await fetch(`${API_URL}/battle/reset`, { method: "POST" });
}
