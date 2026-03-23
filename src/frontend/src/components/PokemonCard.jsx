import "../styles/pokemonCard.css";

function PokemonCard({ name, hp, isTurn, onAttack }) {
  const hpPercent = (hp / 100) * 100;

  return (
    <div className="pokemonCard">
      <h2 className="pokemonName">{name}</h2>

      <div className="hpContainer">
        <div className="hpLabel">HP</div>

        <div className="hpBarBackground">
          <div
            className="hpBar"
            style={{ width: `${hpPercent}%` }}
          ></div>
        </div>
      </div>

      <button
        className="attackButton"
        disabled={!isTurn}
        onClick={onAttack}
      >
        ATACAR
      </button>
    </div>
  );
}

export default PokemonCard;