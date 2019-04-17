const admin = require('firebase-admin');
const functions = require('firebase-functions');
const request = require('request')


admin.initializeApp(functions.config().firebase);

var db = admin.firestore();

// Define your HTML/CSS
// Create an image by sending a POST to the API.
// Retrieve your api_id and api_key from the Dashboard. https://htmlcsstoimage.com/dashboard
// img_url = '';
// request.post({ url: 'https://hcti.io/v1/image', form: data})
//   .auth('9a01fd95-e431-4003-8a53-f3af816957fd', '9f623ec6-e868-4126-bfa0-4c323e5e5713')
//   .on('data', function(data) {
//     resp = JSON.parse(data)
//     console.log(resp['url']);
//     img_url = resp['url'];
//   })

exports.helloWorld = functions.https.onRequest((req, res) => {
  let phoneNumber = req.query.phoneNumber;
  let profile_url = req.query.profile_url;

  console.log(phoneNumber);
  console.log(profile_url);
  // let phoneNumber = "+919654182983";
  // let profile_url = "https://firebasestorage.googleapis.com/v0/b/project470-647d6.appspot.com/o/%2B919654182983?alt=media&token=878d709a-fcc7-475f-96bf-d3f07ddce347";
  var userRef = db.collection('userInfo').doc(phoneNumber);
  var getDoc = userRef.get()
    .then(doc => {
      if (!doc.exists) {
        console.log('No such document!');
        res.status(400).send('No such document!');
      } else {
        console.log('Document data:', doc.data());
        console.log('Document data:', doc.data().experience);
        let user_name = doc.data().user_name;
        let addr2 = doc.data().addr2;
        let experience = doc.data().experience;
        data = {
          html: `<div class="compact-profile">
      <div class="header">
        <div class="profile">
          <div class="title">${user_name}</div>
          <hr>
          <div class="sub-title"><img class="location-icon" src="https://res.cloudinary.com/thinkcdnimages/image/upload/v1553103254/location_e5herv.svg"><span>${addr2}</span></div>
          <div class="highlights">

		<div class="badge">
			<img class="badgebg" src="https://res.cloudinary.com/thinkcdnimages/image/upload/v1553015903/badge_hy9svu.png">
			<div class="number">7</div>
<!--       <div class="number rocket"><img src="https://res.cloudinary.com/thinkcdnimages/image/upload/v1553017498/rocket_i3kmd0.svg"></div> -->
      <div class="text-container"><div class="text">Years Experience</div></div>
		</div>

		<div class="pic" style="background-image: url(${profile_url});">
<!-- 			<img class="img" src="https://anima-uploads.s3.amazonaws.com/projects/5c90c5dbe0a593000bd612a4/releases/5c90fb4b449d31000932ba59/img/compact-profile-img@2x.png"> -->
</div>
		</div>
        </div>
      </div>
      <div class="content">
        <div class="section">
          <div class="title">
            Hard Skills
          </div>
          <div class="tags">
            <div class="tag bg-primary-light text-primary">
              ડ્રાઇવિંગ (Driving)
            </div>
            <div class="tag bg-primary-light text-primary">
              ડિલિવરી(Delivery)
            </div>
          </div>
      </div>
        <div class="section m-top">
          <div class="title">
            Soft Skills
          </div>
          <div class="tags flex">
            <div class="tag bg-primary-light text-primary flex col justify-center align-center">
              <div>Working Memory</div>
              <div class="stars flex justify-center">
                <span class="star filled"></span>
                <span class="star filled"></span>
                <span class="star filled"></span>
                <span class="star empty"></span>
                <span class="star empty"></span>
              </div>
            </div>
            <div class="tag bg-primary-light text-primary flex col justify-center align-center">
              <div>Basic Maths</div>
              <div class="stars flex justify-center">
                <span class="star filled"></span>
                <span class="star filled"></span>
                <span class="star filled"></span>
                <span class="star filled"></span>
                <span class="star empty"></span>
              </div>
            </div>
          </div>
      </div>
        <div class="section m-top">
          <div class="title">
            Hobbies
          </div>
          <div class="tags">
            <div class="tag bg-primary-light text-primary">
              Travel (પ્રવાસ)
            </div>
            <div class="tag bg-primary-light text-primary">
              Movies/TV shows(મૂવીઝ)
            </div>
          </div>
      </div>
    </div>
  <div class="footer">
    <div class="logo"><img src="https://res.cloudinary.com/thinkcdnimages/image/upload/v1553080609/logo-long-35h-www_b3ldjo.png"></div>
    <div class="title">Currently working at</div>
    <div class="text">Uber</div>
    <hr>
    <div class="tags">
      <div class="tag bg-white text-primary mb-0 flex align-center">
        <img class="phone-icon" src="https://res.cloudinary.com/thinkcdnimages/image/upload/v1553103062/phone_v0hjpk.svg">${phoneNumber}</div>
    </div>

      </div>`,
          css: `.compact-profile {
  width: 540px;
  min-height: 960px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  border: 1px solid #8944c2;
  border-radius: 4px;
}
.header {
  position: relative;
  display: flex;
  flex-direction: column;
  padding-left: 30px;
  padding-right: 30px;
  padding-top: 25px;
  background-image: linear-gradient(to right bottom, #3023ae, #6332b8, #8944c2, #aa57cc, #c86dd7);
  margin-bottom:145px;
}

.profile {
  .title {
    position: relative;
    height: auto;
    margin: 0;
    -ms-transform: rotate(0);
    -webkit-transform: rotate(0);
    transform: rotate(0);
    // font-family: 'GoogleSans-Regular',Helvetica,Arial,serif;
    font-size: 35.0px;
    color: rgba(255,255,255,1.0);
    text-align: left;
    letter-spacing: .27px;
    line-height: 45.0px;
  }
  .sub-title {
    position: relative;
    height: auto;
    margin: 7px 0 0 0;
    -ms-transform: rotate(0);
    -webkit-transform: rotate(0);
    transform: rotate(0);
    // font-family: 'GoogleSans-Regular',Helvetica,Arial,serif;
    font-size: 20.0px;
    color: rgba(211,211,211,1.0);
    text-align: left;
    letter-spacing: .23px;
    line-height: 25.0px;
    padding-bottom: 135px;
    display: flex;
    align-items: center;
  }
  .highlights {
    height: 250px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: absolute;
    bottom: -125px;
    width: calc(100% - 60px);
    .badge {
      width: 167px;
      height: 167px;
      img {
        width: 167px;
      height: 167px;
      }
      .number {
    bottom: 116px;
    height: auto;
    width: 68px;
    position: absolute;
    margin: 0;
    left: 52px;
    -ms-transform: rotate(0);
    -webkit-transform: rotate(0);
    transform: rotate(0);
    // font-family: 'GoogleSans-Bold',Helvetica,Arial,serif;
    font-size: 60.0px;
    color: rgba(143,18,253,1.0);
    text-align: center;
    letter-spacing: .47px;
    line-height: 76.0px;
        &.rocket img {
          width: 40px;
          height: 40px;
        }
}
      .text-container {
    display: flex;
        align-items: center;
        justify-content: center;
    bottom: 79px;
    height: 38px;
    width: 135px;
    position: absolute;
    margin: 0;
    left: 17px;
    -ms-transform: rotate(0);
    -webkit-transform: rotate(0);
    transform: rotate(0);
        .text {
          // font-family: 'GoogleSans-Bold',Helvetica,Arial,serif;
    font-size: 18.0px;
    color: rgba(143,18,253,1.0);
    letter-spacing: .47px;
    line-height: 19.0px;
          text-align: center;
        }
}
    }
    .pic {
      width: 250px;
      height: 250px;
      border: 3px solid rgba(189,16,224,0.6);
      border-radius: 24px;
      background-repeat: no-repeat;
      background-position: center center;
      background-size: cover;

      // img {
      //   border-radius: 20px;
      //   width: 250px;
      // height: 250px;
      // }
    }
  }
  }

.content {
  padding: 10px 30px;
  position: relative;
  display: flex;
  flex-direction: column;
}

.section {
  .title {
    height: auto;
    width: auto;
    margin: 0;
    -ms-transform: rotate(0);
    -webkit-transform: rotate(0);
    transform: rotate(0);
    // font-family: 'GoogleSans-Regular',Helvetica,Arial,serif;
    font-size: 25.0px;
    color: rgba(143,18,253,1.0);
    text-align: left;
    letter-spacing: .29px;
    line-height: 32.0px;
    margin-bottom: 10px;
  }
  &.m-top {
    margin-top: 10px;
  }
}

.footer {
  position: relative;
  background-image: linear-gradient(to left top, #3023ae, #6332b8, #8944c2, #aa57cc, #c86dd7);
  padding: 15px 30px 15px 30px;
  .title {
    background-color: rgba(255,255,255,0.0);
    position: relative;
    height: auto;
    margin: 0;
    -ms-transform: rotate(0);
    -webkit-transform: rotate(0);
    transform: rotate(0);
    // font-family: 'GoogleSans-Regular',Helvetica,Arial,serif;
    font-size: 20.0px;
    color: rgba(233,233,233,1.0);
    text-align: left;
    letter-spacing: .29px;
    line-height: 25.0px;

}
  .text {
    position: relative;
    height: auto;
    margin: 8px 0 0 0;
    -ms-transform: rotate(0);
    -webkit-transform: rotate(0);
    transform: rotate(0);
    // font-family: 'GoogleSans-Regular',Helvetica,Arial,serif;
    font-size: 25.0px;
    color: rgba(255,255,255,1.0);
    text-align: left;
    letter-spacing: 1.25px;
    line-height: 32.0px;
}

}

.tags {
    display: flex;
    flex-wrap: wrap;
    .tag {
      width: auto;
          padding: 5px 10px;
    margin-right: 10px;
    border-radius: 4px;
    margin-bottom: 10px;
      -ms-transform: rotate(0);
    -webkit-transform: rotate(0);
    transform: rotate(0);
    // font-family: 'GoogleSans-Medium',Helvetica,Arial,serif;
    font-size: 18.0px;
    color: rgba(143,18,253,1.0);
    text-align: left;
    letter-spacing: 1.0px;
    line-height: 25.0px;
    }
  }

.stars {
  .star {
    height: 30px;
    width: 30px;
    margin: 2px 3px;
    &.filled {
      background-image: url('https://res.cloudinary.com/thinkcdnimages/image/upload/v1553012989/star-filled_hjdxsi.svg');
      background-repeat: no-repeat;
      background-position: center;
    }
    &.empty {
      background-image: url('https://res.cloudinary.com/thinkcdnimages/image/upload/v1553010863/star-empty_yfpbaf.svg');
      background-repeat: no-repeat;
      background-position: center;
    }
  }
}

.bg-white {
  background-color: white;
}

.bg-primary-light {
  background-color: rgba(144,19,254, 0.2);
}

.text-primary {
  color: #9013FE;
}

.text-center {
  text-align: center;
}

.flex {
  display: flex;
  &.col {
    flex-direction: column;
  }
}

.justify-center {
  justify-content: center;
}

.justify-between {
  justify-content: space-between;
}

.align-center {
  align-items: center;
}

.mb-0 {
  margin-bottom: 0 !important;
}

hr {
      border-color: #D3D3D3;
    box-shadow: none;
    border-style: solid;
}

.footer .logo {
  position: absolute;
  bottom: 15px;
  right: 30px;
}

.footer .logo img {
  height: 20px;
  opacity: 0.6;
  padding-bottom: 5px;
}

.phone-icon {
  height: 24px;
  margin-right: 6px;
}

.location-icon {
      height: 20px;
      margin-bottom: 4px;
      margin-right: 6px;
    }`,
          google_fonts: "Roboto"
        };

        // if (request.body.message === undefined) {
        //   // This is an error case, as "message" is required.
        //   response.status(400).send('No message defined!');
        // } else {
          // Everything is okay.
          // console.log(req.body.message);
          request.post({ url: 'https://hcti.io/v1/image', form: data})
            .auth('fd221d57-2089-412a-872c-426c1910527e', '7ab37bf4-c709-46e5-9ecd-f61aa86761d7')
            .on('data', function(data) {
              // console.log('error:', error); // Print the error if one occurred
              // console.log('statusCode:', response && response.statusCode); // Print the response status code if a response was received
              console.log('data:', data); //Prints the response of the request.
              resp = JSON.parse(data);
              console.log(resp['url']);
              res.status(200).send(resp['url']);
        });
      }
    })
    .catch(err => {
      console.log('Error getting document', err);
      res.status(500).send('No such document!');
    });
});


// exports.generateSmartResume = functions.firestore
//   .document('/userInfo/{phone_number}')
//   .onWrite((change, context) => {
//     const user_id = context.params.user_id;
//     const notification_id = context.params.notification_id;
//
//     console.log('We have a notification from : ', user_id);
//
//     var userRef = firestore.collection('Users').doc(user_id);
//     return userRef
//       .get()
//       .then(doc => {
//         if (!doc.exists) {
//           console.log('No such User document!');
//           throw new Error('No such User document!'); //should not occur normally as the notification is a "child" of the user
//         } else {
//           console.log('Document data:', doc.data());
//           console.log('Document data:', doc.data().token_id);
//           return true;
//         }
//       })
//       .catch(err => {
//         console.log('Error getting document', err);
//         return false;
//       });
//   });
