var app = require("express")();
var server = require("http").Server(app);
var io = require("socket.io")(server);

let room_list = new Object(); // acts as a hashtable
let current_players = 0;
let room_id_marker = 1;

server.listen(8080, function(){
    console.log("Server for tetris is now up and running.");
})

io.on("connection", function(socket){
    current_players++;
    socket.emit("socket_id", {id: socket.id});
    socket.emit("current_players", {current_players: current_players});
    socket.emit("get_room_list", {room_list: room_list});
    socket.on("refresh_room_list", function(){
        socket.emit("room_list", {room_list: room_list, current_players: current_players});
    });
    socket.on("disconnect", function(){
        current_players--;
    });

    //for creating/joining/leaving rooms
    socket.on("create_room", function(room_title){
        let room = create_room(room_title, socket.id);
        socket.emit("created_room", {room_list: room_list, room_id: room.id});
    });
    socket.on("join_room", function(room_id){
        if(room_list[room_id] != undefined && join_room(socket.id, room_id)){
            socket.emit("message", {message: "success", room_list: room_list, room_id: room_id});

            //other players need to be informed of the joining gamer
            let room = room_list[room_id];
            for(let i = 0; i < room.player_list.length; i++){
                io.sockets[room.player_list[i]].emit("refresh_room", {room_list: room_list, room_id: room_id});
            }
        }
        else{
            socket.emit("message", {message: "failure", room_list: room_list});
        }
    });
    socket.on("leave_room", function(room_id){
        leave_room(room_id, socket.id);
        socket.emit("room_list", {room_list: room_list, current_players: current_players});

        //other players need to be informed of the leaving gamer
        let room = room_list[room_id];
        for(let i = 0; i < room.player_list.length; i++){
            io.sockets[room.player_list[i]].emit("refresh_room", {room_list: room_list, room_id: room_id});
        }
    });

    //for multiplayer tetris gameplay
    //TODO

});

function create_room(room_title, player_id){
    let room = new Room(room_id_marker++, room_title, player_id);
    //room_id_marker might overlfow when too many rooms are created since server launch
    room_list[room.id] = room;
    return room;
}

function join_room(player_id, room_id){
    let room = room_list[room_id];
    if(room.players.length >= 2){
        return false;
    }
    room.players.push(player_id);
    return true;
}

function leave_room(room_id, player_id){
    let player_list = room_list[room_id].players;
    for(let i = 0; i < player_list.length; i++){
        if(player_list[i] == player_id){
            player_list.splice(i,1);
            break;
        }
    }
}

class Room{
    constructor(id, title, player_id){
        this.id = id;
        this.title = title;
        this.players = [player_id];
        this.is_playing = false;
        this.max_players = 2;
    }
}