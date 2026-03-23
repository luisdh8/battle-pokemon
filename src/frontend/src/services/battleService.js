const API_URL = "http://localhost:8080";

export async function getBattle() {
  const response = await fetch(`${API_URL}/battle`);
  return await response.json();
}

export async function attack(attacker, damage) {
  const response = await fetch(`${API_URL}/battle/attack`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ attacker, damage }),
  });

  return await response.json();
}