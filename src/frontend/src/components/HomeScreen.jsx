import "../styles/homeScreen.css";

/**
 * Props:
 *   selectedIndex  0 = Crear, 1 = Unirse
 *   onCreate       () => void
 *   onJoin         () => void
 */
const HomeScreen = ({ selectedIndex, onCreate, onJoin }) => {
  const options = [
    { label: "CREAR BATALLA", action: onCreate },
    { label: "UNIRSE A BATALLA", action: onJoin },
  ];

  return (
    <div className="home-screen">
      <p className="home-title">POKEMON<br />BATTLE</p>
      <div className="home-buttons">
        {options.map((opt, i) => (
          <button
            key={opt.label}
            className={`home-btn ${selectedIndex === i ? "home-btn--active" : ""}`}
            onClick={opt.action}
          >
            {selectedIndex === i ? "▶ " : "  "}{opt.label}
          </button>
        ))}
      </div>
      <p className="home-hint">↕ D-pad · A o SELECT para confirmar</p>
    </div>
  );
};

export default HomeScreen;
