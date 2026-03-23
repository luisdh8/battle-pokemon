import { useEffect, useState } from "react";
import "./styles/app.css";
import PokemonCard from "./components/PokemonCard";
import { generateRandomDamage } from "./utils/randomDamage";
import { getBattle, attack } from "./services/battleService";

function App() {
  const [battle, setBattle] = useState(null);

  useEffect(() => {
    loadBattle();
  }, []);

  async function loadBattle() {
    const data = await getBattle();
    setBattle(data);
  }

  async function handleAttack(attacker) {
    const damage = generateRandomDamage();

    const updatedBattle = await attack(attacker, damage);

    setBattle(updatedBattle);
  }

  if (!battle) {
    return <p>Loading battle...</p>;
  }

  return (
    <div className="appContainer">
      <h1 className="title">Pokemon Battle</h1>

      <div className="battleField">
        <PokemonCard
          name={battle.pokemon1.name}
          hp={battle.pokemon1.hp}
          isTurn={battle.turn === "pokemon1"}
          onAttack={() => handleAttack("pokemon1")}
        />

        <PokemonCard
          name={battle.pokemon2.name}
          hp={battle.pokemon2.hp}
          isTurn={battle.turn === "pokemon2"}
          onAttack={() => handleAttack("pokemon2")}
        />
      </div>

      <div className="turn">
        Turno actual: {battle.turn}
      </div>
    </div>
  );
}

export default App;