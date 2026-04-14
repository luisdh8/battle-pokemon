import { useEffect, useRef } from "react";
import { getBattleStatus } from "../services/battleService";
import "../styles/waitingScreen.css";

/**
 * Pantalla intermedia: espera a que el segundo jugador seleccione su pokémon.
 * Hace polling a GET /battle/status cada 2 segundos.
 * Cuando registered >= 2 → llama onReady().
 *
 * Props:
 *   slot      "pokemon1" | "pokemon2"  (para mostrar qué slot es el jugador)
 *   onReady   () => void
 */
const WaitingScreen = ({ slot, onReady }) => {
  const intervalRef = useRef(null);

  useEffect(() => {
    async function poll() {
      const { registered } = await getBattleStatus();
      if (registered >= 2) {
        clearInterval(intervalRef.current);
        onReady();
      }
    }
    poll();
    intervalRef.current = setInterval(poll, 2000);
    return () => clearInterval(intervalRef.current);
  }, [onReady]);

  return (
    <div className="wait-screen">
      <p className="wait-title">ESPERANDO...</p>
      <div className="wait-dots">
        <span className="dot d1">·</span>
        <span className="dot d2">·</span>
        <span className="dot d3">·</span>
      </div>
      <p className="wait-info">
        Tú eres: <strong>{slot === "pokemon1" ? "JUGADOR 1" : "JUGADOR 2"}</strong>
      </p>
      <p className="wait-hint">
        Esperando al oponente para<br />comenzar la batalla...
      </p>
    </div>
  );
};

export default WaitingScreen;
