const Actions = ({ onA, onB }) => {
  return (
    <div className="buttons">
      <div className="button a" onClick={onA}>A</div>
      <div className="button b" onClick={onB}>B</div>
    </div>
  );
};

export default Actions;
