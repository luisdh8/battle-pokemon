import { useCallback, useEffect, useRef, useState } from "react";
import "./styles/app.css";
import Screen      from "./components/Screen";
import Dpad        from "./components/buttons/Dpad";
import Actions     from "./components/buttons/Actions";
import StartSelect from "./components/buttons/StartSelect";
import {
  getBattle,
  attack,
  selectPokemon,
  resetBattle,
} from "./services/battleService";
import { generateRandomDamage } from "./utils/randomDamage";

const PAGE_SIZE   = 6;
const POLL_MS     = 2500;
const POKEAPI_URL = "https://pokeapi.co/api/v2/pokemon?limit=151";

export default function App() {
  // ── Vista actual ──────────────────────────────────────────────────────────
  const [view, setView] = useState("home");   // home | select | waiting | battle
  const [slot, setSlot] = useState(null);     // "pokemon1" | "pokemon2"

  // ── Home: botón resaltado ─────────────────────────────────────────────────
  const [homeIndex, setHomeIndex] = useState(0);

  // ── Lista de pokémon (cargada en background una sola vez) ─────────────────
  const [pokemons,       setPokemons]       = useState([]);
  const [pokemonsLoaded, setPokemonsLoaded] = useState(false);

  // ── Navegación en SelectScreen ────────────────────────────────────────────
  const [pageIndex,     setPageIndex]     = useState(0);
  const [selectedIndex, setSelectedIndex] = useState(0);

  // ── Batalla ───────────────────────────────────────────────────────────────
  const [battle, setBattle] = useState(null);
  const pollRef = useRef(null);

  // ── Carga pokémon al montar ───────────────────────────────────────────────
  useEffect(() => {
    (async () => {
      try {
        const res  = await fetch(POKEAPI_URL);
        const data = await res.json();
        const detailed = await Promise.all(
          data.results.map(p => fetch(p.url).then(r => r.json()))
        );
        setPokemons(detailed);
        setPokemonsLoaded(true);
      } catch (err) {
        console.error("Error cargando pokémon:", err);
      }
    })();
  }, []);

  // ── Polling de batalla (solo cuando view === "battle") ────────────────────
  useEffect(() => {
    if (view !== "battle") {
      clearInterval(pollRef.current);
      return;
    }
    loadBattle();
    pollRef.current = setInterval(loadBattle, POLL_MS);
    return () => clearInterval(pollRef.current);
  }, [view]);

  async function loadBattle() {
    try {
      const data = await getBattle();
      setBattle(data);
    } catch (err) {
      console.error("Error cargando batalla:", err);
    }
  }

  // ── Helpers de navegación ─────────────────────────────────────────────────
  const maxPage = Math.max(0, Math.ceil(pokemons.length / PAGE_SIZE) - 1);

  function currentSelectedPokemon() {
    const start = pageIndex * PAGE_SIZE;
    return pokemons.slice(start, start + PAGE_SIZE)[selectedIndex] ?? null;
  }

  // ── Handlers de home ──────────────────────────────────────────────────────
  async function handleCreate() {
    try {
      await resetBattle();        // limpia Firestore; si falla, continúa igual
    } catch (err) {
      console.warn("resetBattle falló (puede ignorarse):", err);
    }
    setBattle(null);
    setSlot("pokemon1");
    setPageIndex(0);
    setSelectedIndex(0);
    setView("select");            // navega siempre, con o sin error de reset
  }

  function handleJoin() {
    setSlot("pokemon2");
    setPageIndex(0);
    setSelectedIndex(0);
    setView("select");
  }

  // ── Confirmar pokémon elegido ─────────────────────────────────────────────
  async function handleSelectConfirm(name, hp) {
    try {
      const ok = await selectPokemon(slot, name, hp);
      if (ok) setView("waiting");
      else    console.error("selectPokemon devolvió false");
    } catch (err) {
      console.error("Error al seleccionar pokémon:", err);
    }
  }

  // ── Waiting → Battle ──────────────────────────────────────────────────────
  const handleWaitReady = useCallback(() => setView("battle"), []);

  // ── Ataque ────────────────────────────────────────────────────────────────
  async function handleAttack(attacker) {
    clearInterval(pollRef.current);
    try {
      const damage  = generateRandomDamage();
      const updated = await attack(attacker, damage);
      setBattle(updated);
    } catch (err) {
      console.error("Error en ataque:", err);
    } finally {
      pollRef.current = setInterval(loadBattle, POLL_MS);
    }
  }

  // ── Volver al menú principal ──────────────────────────────────────────────
  function handleGoHome() {
    clearInterval(pollRef.current);
    setBattle(null);
    setSlot(null);
    setView("home");
  }

  // ── D-pad ─────────────────────────────────────────────────────────────────
  function handleUp() {
    if (view === "home")   setHomeIndex(0);
    if (view === "select") { setPageIndex(p => Math.max(0, p - 1)); setSelectedIndex(0); }
  }
  function handleDown() {
    if (view === "home")   setHomeIndex(1);
    if (view === "select") { setPageIndex(p => Math.min(maxPage, p + 1)); setSelectedIndex(0); }
  }
  function handleLeft() {
    if (view === "select") setSelectedIndex(i => Math.max(0, i - 1));
  }
  function handleRight() {
    if (view === "select") setSelectedIndex(i => Math.min(PAGE_SIZE - 1, i + 1));
  }

  // ── Botón A y SELECT ──────────────────────────────────────────────────────
  function handleA() {
    if (view === "home") {
      homeIndex === 0 ? handleCreate() : handleJoin();

    } else if (view === "select") {
      const poke = currentSelectedPokemon();
      if (poke) handleSelectConfirm(poke.name, poke.stats[0].base_stat);

    } else if (view === "battle" && battle && !battle.winner) {
      handleAttack(battle.turn);
    }
  }

  // ── Render ────────────────────────────────────────────────────────────────
  return (
    <div className="gameboy">
      <Screen
        view={view}
        slot={slot}
        battle={battle}
        homeIndex={homeIndex}
        pokemons={pokemons}
        pokemonsLoaded={pokemonsLoaded}
        selectedIndex={selectedIndex}
        pageIndex={pageIndex}
        handlers={{
          onCreate:        handleCreate,
          onJoin:          handleJoin,
          onSelectConfirm: handleSelectConfirm,
          onWaitReady:     handleWaitReady,
          onAttack:        handleAttack,
          onGoHome:        handleGoHome,
        }}
      />

      <div className="controls">
        <div className="top-buttons">
          <Dpad
            onUp={handleUp}
            onDown={handleDown}
            onLeft={handleLeft}
            onRight={handleRight}
          />
          <Actions onA={handleA} onB={null} />
        </div>
        <StartSelect onSelect={handleA} />
        <div className="start-select-labels">
          <div className="select-label">SELECT</div>
          <div className="start-label">START</div>
        </div>
      </div>
    </div>
  );
}
