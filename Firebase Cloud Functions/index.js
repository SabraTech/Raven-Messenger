'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const nodemailer = require('nodemailer');

const gmailEmail = functions.config().gmail.email;
const gmailPassword = functions.config().gmail.password;
const mailTransport = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: gmailEmail,
        pass: gmailPassword,
    },
});

const APP_NAME = 'Raven Messenger';

exports.sendRequestNotification = functions.database.ref('/notifications/{receiver_id}/{sender_id}').onWrite((event) => {
    const receiver_id = event.params.receiver_id;
    const from_sender_id = event.params.sender_id;
    console.log('we have notification to send to : ', receiver_id);
    if (!event.data.val()) {
        return console.log('A notification has been deleted from data base: ', receiver_id);
    }


    console.log('you have notification from: ', from_sender_id);

    //get sender name
    const senderUser = admin.database().ref(`/user/${from_sender_id}/name`).once('value');
    return senderUser.then(senderUserNameResult => {
        const senderUserName = senderUserNameResult.val();
        //get token of receiver device
        const token = admin.database().ref(`/user/${receiver_id}/token`).once('value');
        return token.then(result => {
            const token_id = result.val();
            console.log('token::   ', token_id);
            const payload = {
                data: {
                    title: "New Friend request",
                    body: `${senderUserName} has sent you a friend request `,
                    from_sender_id: from_sender_id
                }
            };

            //send notification
            return admin.messaging().sendToDevice(token_id, payload).then(response => {
                console.log("Notification sent :: ", token_id);
                return response;
            });
        });
    });
});


exports.sendChatNotification = functions.database.ref('/message/{room_id}/{message_id}').onWrite((event) => {

    const room_id = event.params.room_id;
    const message_id = event.params.message_id;
    console.log('we have notification to send to the message id : ', message_id);
    if (!event.data.val()) {
        return console.log('A notification has been deleted from data base: ', room_id);
    }
    // get receiver id
    const receiver_id = admin.database().ref(`/message/${room_id}/${message_id}/idReceiver`).once('value');
    return receiver_id.then(receiverResponse => {
        const to_user_id = receiverResponse.val();

        // get sender id
        const sender_id = admin.database().ref(`/message/${room_id}/${message_id}/idSender`).once('value');
        return sender_id.then(senderResponse => {
            const from_user_id = senderResponse.val();

            const sender_data = admin.database().ref(`/user/${from_user_id}`).once('value');
            return sender_data.then(dataResult => {
                const name = dataResult.val().name;
                const avatar = dataResult.val().avatar;
                // get device token
                const token = admin.database().ref(`/user/${to_user_id}/token`).once('value');
                return token.then(result => {
                    const token_id = result.val();
                    const payload = {
                        data: {
                            title: "New Message",
                            body: `${name} has sent a message`,
                            from_user_id: from_user_id,
                            name: name,
                            avatar: avatar,
                            id_room: room_id
                        }
                    };

                    //send the notification
                    return admin.messaging().sendToDevice(token_id, payload).then(sendResponse => {
                        console.log("Notification sent :: ", token_id);
                        return senderResponse;
                    });
                });
            });
        });
    });
});


exports.sendWelcomeEmail = functions.database.ref('/user/{user_id}').onCreate((event) => {
    const user_id = event.params.user_id;
    const user = admin.database().ref(`/user/${user_id}`).once('value');
    return user.then(user_data => {
        const email = user_data.val().email;
        const displayName = user_data.val().name;
        return sendEmail(email, displayName);
    });
});


function sendEmail(email, displayName) {
    const mailOptions = {
        from: `${APP_NAME} <noreply@firebase.com`,
        to: email,
    };

    mailOptions.subject = `Welcome to ${APP_NAME} !`;
    mailOptions.text = `Hello ${displayName || ' '}! \n\n Welcome to ${APP_NAME}. We all of Raven Team hope you will enjoy our service. \n\n Thanks,\nYour Raven Messenger team`;
    return mailTransport.sendMail(mailOptions).then(() => {
        return console.log('New welcome email sent to:', email);
    });
}