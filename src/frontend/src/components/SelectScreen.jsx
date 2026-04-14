import "../styles/selectScreen.css";

const PAGE_SIZE = 6;

/**
 * Lista de pokémon paginada. Ya no hace fetch interno;
 * recibe la lista completa desde App.jsx.
 *
 * Props:
 *   slot          "pokemon1" | "pokemon2"
 *   pokemons      array completo de pokémon (de PokeAPI)
 *   loaded        boolean — si aún está cargando
 *   selectedIndex índice activo dentro de la página actual
 *   pageIndex     página activa
 *   onConfirm     (name, hp) => void
 */
const SelectScreen = ({ slot, pokemons, loaded, selectedIndex, pageIndex, onConfirm }) => {
  if (!loaded) return <p className="sel-loading">Cargando pokémon...</p>;

  const maxPage     = Math.max(0, Math.ceil(pokemons.length / PAGE_SIZE) - 1);
  const start       = pageIndex * PAGE_SIZE;
  const currentPage = pokemons.slice(start, start + PAGE_SIZE);

  function handleClick(pokemon) {
    onConfirm(pokemon.name, pokemon.stats[0].base_stat);
  }

  return (
    <div className="sel-screen">
      <p className="sel-header">
        {slot === "pokemon1" ? "JUGADOR 1" : "JUGADOR 2"} — Elige tu Pokémon
      </p>

      <div className="sel-grid">
        {currentPage.map((pokemon, i) => (
          <div
            key={pokemon.id}
            className={`sel-card ${selectedIndex === i ? "selected" : ""}`}
            onClick={() => handleClick(pokemon)}
          >
            <img
              src={pokemon.sprites.front_default}
              alt={pokemon.name}
              className="sel-sprite"
            />
            <span className="sel-name">{pokemon.name}</span>
          </div>
        ))}
      </div>

      <p className="sel-footer">
        Pág {pageIndex + 1}/{maxPage + 1} · A o SELECT para confirmar
      </p>
    </div>
  );
};

export default SelectScreen;
