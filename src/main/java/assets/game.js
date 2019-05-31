var isSetup = true;
var placedShips = 0;
var game;
var shipType;
var vertical;
var isRadar = false;
var isLaser = false;
var radarUsed = 0;
var numSunk = 0;
var moveTimes = 0;
var moveDir = 0; // 1-north, 2-south, 3-east, 4-west

function makeGrid(table, isPlayer) {
    for (i=0; i<10; i++) {
        let row = document.createElement('tr');
        for (j=0; j<10; j++) {
            let column = document.createElement('td');
            column.addEventListener("click", cellClick);
            row.appendChild(column);
        }
        table.appendChild(row);
    }
}

function markHits(board, elementId, surrenderText) {
    board.attacks.forEach((attack) => {
        // remove radar detect
        document.getElementById(elementId).rows[attack.location.row - 1].cells[attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.remove("radar-square");
        document.getElementById(elementId).rows[attack.location.row - 1].cells[attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.remove("found")

        let className;
        if (attack.result === "MISS")
            className = "miss";
        else if (attack.result === "HIT")
            className = "hit";
        else if (attack.result === "SUNK") {
            className = "sink"
            if (elementId === "opponent") {
                numSunk ++;
                if (numSunk === 1) {
                    document.getElementById("radar_btn").style.visibility = "visible";
                    document.getElementById("laser_btn").style.visibility = "visible";
                }
                if (numSunk === 2) {
                    document.getElementById("up_btn").style.visibility = "visible";
                    document.getElementById("down_btn").style.visibility = "visible";
                    document.getElementById("left_btn").style.visibility = "visible";
                    document.getElementById("right_btn").style.visibility = "visible";
                }

                var square;

                for (square of attack.ship.occupiedSquares) {
                    if(square.type == "CQ"){
                        document.getElementById(elementId).rows[square.row - 1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.remove("miss");

                    }else{
                        document.getElementById(elementId).rows[square.row - 1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("sink");
                        document.getElementById(elementId).rows[square.row - 1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.remove("found");
                        document.getElementById(elementId).rows[square.row - 1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.remove("hit");
                    }
                }
            }
        } else if (attack.result === "SURRENDER") {
            alert(surrenderText);
            var square;

            for (square of attack.ship.occupiedSquares) {
                if(square.type == "CQ"){
                    document.getElementById(elementId).rows[square.row - 1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.remove("miss");

                }else{
                    document.getElementById(elementId).rows[square.row - 1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("sink");
                    document.getElementById(elementId).rows[square.row - 1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.remove("found");
                    document.getElementById(elementId).rows[square.row - 1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.remove("hit");
                }
            }
        } else if (attack.result === "RADAR") {
            let table = document.getElementById(elementId);
            let row = attack.location.row - 1;
            let col = attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0);
            let tableRow;
            let cell;
            let i;
            for (i = 0; i < 5; i++) {
                if (table.rows[row - 2 + i] === undefined 
                        || table.rows[row - 2 + i].rowIndex === 0) {
                    continue;
                }
                // start from 2 rows down
                tableRow = table.rows[row - 2 + i];
                if (tableRow != undefined) {
                    cell = tableRow.cells[col];
                    if (!(cell.classList.contains("hit") 
                          || cell.classList.contains("miss") 
                          || cell.classList.contains("sink"))) {
                        if (cell.classList.contains("opp_occupied")) {
                            cell.classList.add("found");
                        }
                        else {
                            cell.classList.add("radar-square");
                        }
                    }
                }

                // place the 4 squares diagonal to middle
                if (i === 1 || i === 3) {
                    if ((tableRow.cells[col - 1] != undefined 
                         && col - 1 != 0) 
                         && !(tableRow.cells[col - 1].classList.contains("hit") 
                             || tableRow.cells[col - 1].classList.contains("miss") 
                             || tableRow.cells[col - 1].classList.contains("sink"))) {
                        if (tableRow.cells[col - 1].classList.contains("opp_occupied")) {
                            tableRow.cells[col - 1].classList.add("found");
                        } else {
                            tableRow.cells[col - 1].classList.add("radar-square");
                        }

                    }
                    if (tableRow.cells[col + 1] != undefined 
                          && !(tableRow.cells[col + 1].classList.contains("hit") 
                            || tableRow.cells[col + 1].classList.contains("miss")
                            || tableRow.cells[col + 1].classList.contains("sink"))) {
                        if (tableRow.cells[col + 1].classList.contains("opp_occupied")) {
                            tableRow.cells[col + 1].classList.add("found");
                        } else {
                            tableRow.cells[col + 1].classList.add("radar-square");
                        }
                    }
                }
            }
            tableRow = table.rows[row];
            for (i = 0; i < 5; i++) {
                if (tableRow.cells[col - 2 + i] === undefined 
                        || tableRow.cells[col - 2 + i].cellIndex === 0) {
                    continue;
                }
                // Start 2 cells left of center
                cell = tableRow.cells[col - 2 + i];
                if (cell != undefined) {
                    if (!(cell.classList.contains("hit") 
                                || cell.classList.contains("miss") 
                                || cell.classList.contains("sink"))) {
                        if (cell.classList.contains("opp_occupied")) {
                            cell.classList.add("found");
                        } else {
                            cell.classList.add("radar-square");
                        }
                    }
                }
            }
            return;
        }

        document.getElementById(elementId).rows[attack.location.row-1].cells[attack.location.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add(className);
    });
}

function redrawGrid() {
    Array.from(document.getElementById("opponent").childNodes).forEach((row) => row.remove());
    Array.from(document.getElementById("player").childNodes).forEach((row) => row.remove());
    makeGrid(document.getElementById("opponent"), false);
    makeGrid(document.getElementById("player"), true);
    if (game === undefined) {
        return;
    }

    game.playersBoard.ships.forEach((ship) => ship.occupiedSquares.forEach((square) => {
        if(square.type === "CQ"){
            document.getElementById("player").rows[square.row - 1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("quarter");
        } else {
            document.getElementById("player").rows[square.row-1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("occupied");
        }
    }));

    game.opponentsBoard.ships.forEach((ship) => ship.occupiedSquares.forEach((square) => {
        document.getElementById("opponent").rows[square.row - 1].cells[square.column.charCodeAt(0) - 'A'.charCodeAt(0)].classList.add("opp_occupied");
    }));

    markHits(game.opponentsBoard, "opponent", "You won the game");
    markHits(game.playersBoard, "player", "You lost the game");
}

var oldListener;
function registerCellListener(f, board) {
    if (board !== "none") {
        let el = document.getElementById(board);
        for (i=0; i<10; i++) {
            for (j=0; j<10; j++) {
                let cell = el.rows[i].cells[j];
                cell.removeEventListener("mouseover", oldListener);
                cell.removeEventListener("mouseout", oldListener);
                cell.addEventListener("mouseover", f);
                cell.addEventListener("mouseout", f);
            }
        }
        oldListener = f;
    }
}

function cellClick() {
    let row = this.parentNode.rowIndex + 1;
    let col = String.fromCharCode(this.cellIndex + 65);
    if (isSetup) {
        sendXhr("POST", "/place", {game: game, shipType: shipType, x: row, y: col, isVertical: vertical}, function(data) {
            game = data;
            redrawGrid();
            placedShips++;
            if (placedShips == 4) {
                isSetup = false;
                registerCellListener((e) => {}, "none");
            }
        });
    } else {
        if (moveDir > 0 && moveTimes < 2) {
          sendXhr("POST", "/move", {game: game, x: row, y: col, dir: moveDir}, function(data) {
              game = data;
              moveTimes++;
              moveDir = 0;
              if (moveDir === 1)
                document.getElementById("up_btn").classList.toggle("btn_toggle");
              if (moveDir === 2)
                document.getElementById("down_btn").classList.toggle("btn_toggle");
              if (moveDir === 3)
                document.getElementById("left_btn").classList.toggle("btn_toggle");
              if (moveDir === 4)
                document.getElementById("right_btn").classList.toggle("btn_toggle");

              if (moveTimes === 2) {
                  document.getElementById("up_btn").style.display = "none";
                  document.getElementById("down_btn").style.display = "none";
                  document.getElementById("left_btn").style.display = "none";
                  document.getElementById("right_btn").style.display = "none";
              }

              redrawGrid();
          })
        } else {
          sendXhr("POST", "/attack", {game: game, x: row, y: col, radar: isRadar, laser: isLaser}, function(data) {
            game = data;
            if (isRadar) {
                radarUsed ++;
                isRadar = false;
                document.getElementById("radar_btn").classList.toggle("btn_toggle");
            } 
            if (radarUsed === 2) {
                document.getElementById("radar_btn").style.display = "none";
            }

            if (isLaser) {
                isLaser = false;
                document.getElementById("laser_btn").classList.toggle("btn_toggle");
            }

            redrawGrid();
          })
        }
    }
}

function sendXhr(method, url, data, handler) {
    var req = new XMLHttpRequest();
    req.addEventListener("load", function(event) {
        if (req.status != 200) {
            alert("Cannot complete the action");
            return;
        }
        handler(JSON.parse(req.responseText));
    });
    req.open(method, url);
    req.setRequestHeader("Content-Type", "application/json");
    req.send(JSON.stringify(data));
}

function place(size) {
    return function() {
        let row = this.parentNode.rowIndex;
        let col = this.cellIndex;
        vertical = document.getElementById("is_vertical").checked;
        let table = document.getElementById("player");

        var bound = size;
        if (size === 5) bound = size - 1;

        for (let i=0; i<bound; i++) {
            let cell;
            if(vertical) {
                let tableRow = table.rows[row+i];
                if (tableRow === undefined) {
                    // ship is over the edge; let the back end deal with it
                    break;
                }
                cell = tableRow.cells[col];
            } else {
                cell = table.rows[row].cells[col+i];
            }
            if (cell === undefined) {
                // ship is over the edge; let the back end deal with it
                break;
            }
            if (size - i === 2) {
                cell.classList.toggle("quarter");
            } else {
                cell.classList.toggle("placed");
            }
        }

        if (size === 5) {
            let cell;
            if (vertical) {
                cell = table.rows[row+2].cells[col+1];
            } else {
                if (table.rows[row-1] === undefined) {
                } else {
                    cell = table.rows[row-1].cells[col+2];
                }
            }
            if (cell === undefined) {
            } else {
                cell.classList.toggle("placed");
            }
        }
    }
}
function radarPlace() {
    return function () {
        let row = this.parentNode.rowIndex;
        let col = this.cellIndex;
        let table = document.getElementById("opponent");

        if (isRadar) {
            let tableRow;
            let cell;
            let i;
            for (i = 0; i < 5; i++) {
                if (table.rows[row - 2 + i] === undefined || table.rows[row - 2 + i].rowIndex === 0) {
                    continue;
                }
                tableRow = table.rows[row - 2 + i];
                if (tableRow != undefined) {
                    cell = tableRow.cells[col];
                    cell.classList.toggle("placed");
                }
                if (i === 1 || i === 3) {
                    if (tableRow.cells[col - 1] != undefined && col - 1 != 0) {
                        tableRow.cells[col - 1].classList.toggle("placed");
                    }
                    if (tableRow.cells[col + 1] != undefined) {
                        tableRow.cells[col + 1].classList.toggle("placed");
                    }
                }
            }
            tableRow = table.rows[row];
            for (i = 0; i < 5; i++) {
                if (tableRow.cells[col - 2 + i] === undefined || tableRow.cells[col - 2 + i].cellIndex === 0) {
                    continue;
                }
                cell = tableRow.cells[col - 2 + i];
                if (cell != undefined) {
                    cell.classList.toggle("placed");
                }

            }
        }
    }
}

function initGame() {
    makeGrid(document.getElementById("opponent"), false);
    makeGrid(document.getElementById("player"), true);
    document.getElementById("place_minesweeper").addEventListener("click", function(e) {
        shipType = "MINESWEEPER";
        registerCellListener(place(2), "player");
    });
    document.getElementById("place_destroyer").addEventListener("click", function(e) {
        shipType = "DESTROYER";
        registerCellListener(place(3), "player");
    });
    document.getElementById("place_battleship").addEventListener("click", function(e) {
        shipType = "BATTLESHIP";
        registerCellListener(place(4), "player");
    });
    document.getElementById("place_submarine").addEventListener("click", function(e) {
        shipType = "SUBMARINE";
        registerCellListener(place(5), "player");
    });
    document.getElementById("radar_btn").addEventListener("click", function(e) {
        let rad = e.target.classList.toggle("btn_toggle");
        if (rad) {
            isRadar = true;
            registerCellListener(radarPlace(), "opponent");
        } else {
            isRadar = false;
            registerCellListener((e) => {}, "none");
        }

    });
    document.getElementById("laser_btn").addEventListener("click", function(e) {
        let rad = e.target.classList.toggle("btn_toggle");
        if (rad) {
            isLaser = true;
        } else {
            isLaser = false;
        }
    });
    document.getElementById("up_btn").addEventListener("click", function(e) {
        let rad = e.target.classList.toggle("btn_toggle");
        if (rad) {
            moveDir = 1;
        }
    });
    document.getElementById("down_btn").addEventListener("click", function(e) {
        let rad = e.target.classList.toggle("btn_toggle");
        if (rad) {
            moveDir = 2;
        }
    });
    document.getElementById("left_btn").addEventListener("click", function(e) {
        let rad = e.target.classList.toggle("btn_toggle");
        if (rad) {
            moveDir = 3;
        }
    });
    document.getElementById("right_btn").addEventListener("click", function(e) {
        let rad = e.target.classList.toggle("btn_toggle");
        if (rad) {
            moveDir = 4;
        }
    });

    sendXhr("GET", "/game", {}, function(data) {
        game = data;
    });

    document.getElementById("restart_game").addEventListener("click", function(e) {
        window.location.reload();
    });
};
