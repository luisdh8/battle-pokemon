import "../styles/screen.css";
import HomeScreen    from "./HomeScreen";
import SelectScreen  from "./SelectScreen";
import WaitingScreen from "./WaitingScreen";
import BattleScreen  from "./BattleScreen";

const Screen = ({
  view,
  slot,
  battle,
  homeIndex,
  pokemons,
  pokemonsLoaded,
  selectedIndex,
  pageIndex,
  handlers,
}) => {
  const { onCreate, onJoin, onSelectConfirm, onWaitReady, onAttack, onGoHome } = handlers;
  const isPlayer2 = slot === "pokemon2";

  return (
    <div className="screen-border">
      <div className="screen">
        {view === "home" && (
          <HomeScreen
            selectedIndex={homeIndex}
            onCreate={onCreate}
            onJoin={onJoin}
          />
        )}

        {view === "select" && (
          <SelectScreen
            slot={slot}
            pokemons={pokemons}
            loaded={pokemonsLoaded}
            selectedIndex={selectedIndex}
            pageIndex={pageIndex}
            onConfirm={onSelectConfirm}
          />
        )}

        {view === "waiting" && (
          <WaitingScreen slot={slot} onReady={onWaitReady} />
        )}

        {view === "battle" && battle && (
          <BattleScreen
            battle={battle}
            onAttack={onAttack}
            isPlayer2={isPlayer2}
            onGoHome={onGoHome}
          />
        )}

        {view === "battle" && !battle && (
          <p className="loading-text">Cargando batalla...</p>
        )}
      </div>

      <div id="logo">
        <span id="logo-GameBoy">POKE BATTLE</span>
      </div>
    </div>
  );
};

export default Screen;
