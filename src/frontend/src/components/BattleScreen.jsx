import { useEffect, useState } from "react";
import "../styles/battleScreen.css";

const POKEAPI = "https://pokeapi.co/api/v2/pokemon";

async function fetchSprite(name) {
  try {
    const res = await fetch(`${POKEAPI}/${name.toLowerCase()}`);
    const data = await res.json();
    return {
      front: data.sprites.front_default,
      back: data.sprites.back_default,
    };
  } catch {
    return { front: null, back: null };
  }
}

const HPBar = ({ current, max }) => {
  const pct = Math.max(0, Math.min(100, (current / max) * 100));
  const color = pct > 50 ? "green" : pct > 20 ? "orange" : "red";
  return (
    <div className="hp-bar">
      <div className="hp-fill" style={{ width: `${pct}%`, backgroundColor: color }} />
    </div>
  );
};

/**
 * isPlayer2: true cuando el jugador es el que se unió (slot === "pokemon2").
 *   Su pokémon (pokemon2) aparece abajo con vista desde atrás,
 *   y el enemigo (pokemon1) aparece arriba con vista frontal.
 * onGoHome: callback para volver al menú principal cuando hay un ganador.
 */
const BattleScreen = ({ battle, onAttack, isPlayer2 = false, onGoHome }) => {
  const [sprites, setSprites] = useState({ pokemon1: {}, pokemon2: {} });
  const [maxHp, setMaxHp]     = useState({ pokemon1: 100, pokemon2: 100 });
  const [attacking, setAttacking] = useState(false);
  const [log, setLog]         = useState(null);

  const { pokemon1, pokemon2, turn, winner } = battle;

  // Desde la perspectiva del jugador:
  //   "myPoke"    = su propio pokémon (abajo)
  //   "enemyPoke" = el rival (arriba)
  const myKey    = isPlayer2 ? "pokemon2" : "pokemon1";
  const enemyKey = isPlayer2 ? "pokemon1" : "pokemon2";
  const myPoke   = isPlayer2 ? pokemon2   : pokemon1;
  const enemyPoke = isPlayer2 ? pokemon1  : pokemon2;

  useEffect(() => {
    let alive = true;
    async function load() {
      const [s1, s2] = await Promise.all([
        fetchSprite(pokemon1.name),
        fetchSprite(pokemon2.name),
      ]);
      if (alive) {
        setSprites({ pokemon1: s1, pokemon2: s2 });
        setMaxHp({ pokemon1: pokemon1.hp, pokemon2: pokemon2.hp });
      }
    }
    load();
    return () => { alive = false; };
  }, [pokemon1.name, pokemon2.name]);

  async function handleAttack(attacker) {
    if (attacking || winner) return;
    setAttacking(true);
    setLog(`${attacker === "pokemon1" ? pokemon1.name : pokemon2.name} atacó!`);
    await onAttack(attacker);
    setTimeout(() => {
      setLog(null);
      setAttacking(false);
    }, 800);
  }

  return (
    <div className="battle-screen">
      {/* Battle zone: enemigo arriba, mi pokémon abajo */}
      <div className="battle-zone">
        {/* Enemigo: info arriba-izquierda, sprite arriba-derecha */}
        <div className="enemy-info">
          <p>{enemyPoke.name.toUpperCase()}</p>
          <HPBar current={enemyPoke.hp} max={maxHp[enemyKey]} />
          <span className="hp-text">{enemyPoke.hp} HP</span>
        </div>
        <div className="enemy-sprite">
          {sprites[enemyKey].front && (
            <img src={sprites[enemyKey].front} alt={enemyPoke.name} />
          )}
        </div>

        {/* Mi pokémon: sprite abajo-izquierda, info abajo-derecha */}
        <div className="player-sprite">
          {sprites[myKey].back && (
            <img src={sprites[myKey].back} alt={myPoke.name} />
          )}
        </div>
        <div className="player-info">
          <p>{myPoke.name.toUpperCase()}</p>
          <HPBar current={myPoke.hp} max={maxHp[myKey]} />
          <span className="hp-text">{myPoke.hp} HP</span>
        </div>
      </div>

      {/* Text / action zone */}
      <div className="text-zone">
        {winner ? (
          <div className="winner-zone">
            <p className="battle-text winner-text">
              🏆 {winner === "draw" ? "¡Empate!" : `${winner.toUpperCase()} wins!`}
            </p>
            <button className="go-home-btn" onClick={onGoHome}>
              ← Menú principal
            </button>
          </div>
        ) : log ? (
          <p className="battle-text">{log}</p>
        ) : (
          <div className="action-list">
            <div
              className={`action-item ${turn === "pokemon1" ? "active-turn" : ""}`}
              onClick={() => turn === "pokemon1" && handleAttack("pokemon1")}
            >
              {turn === "pokemon1" ? "▶ " : ""}
              {pokemon1.name.toUpperCase()} ATACA
            </div>
            <div
              className={`action-item ${turn === "pokemon2" ? "active-turn" : ""}`}
              onClick={() => turn === "pokemon2" && handleAttack("pokemon2")}
            >
              {turn === "pokemon2" ? "▶ " : ""}
              {pokemon2.name.toUpperCase()} ATACA
            </div>
            <div className="turn-indicator">
              Turno: {turn === "pokemon1" ? pokemon1.name : pokemon2.name}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default BattleScreen;
