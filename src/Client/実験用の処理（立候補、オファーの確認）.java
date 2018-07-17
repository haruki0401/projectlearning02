Client ca = new Client();
		if(ca.connectServer()) {
			if(ca.accountRequest("uc", "uc", "Ç≥ÇÒÇ…ÇÒÇﬂ")) {
				ArrayList<String> as = new ArrayList<String>();
				as.add("ìåãûëÂäw");
				as.add("â°ïlçëóßëÂäw");
				ca.sendUserInformation("äwê∂", "ëÂäwê∂", as);

				ca.receiveUserInformation("uc");

				ca.Candidacy("éøñ‚ÇO");
				ca.Candidacy("éøñ‚ÇP");

				ca.receiveCandidate();
				ArrayList<Question> mycandidacy = new ArrayList<Question>();
				mycandidacy = ca.getMyCandidacy();
				for(Question q: mycandidacy) {
					System.out.println(q.getQuestion());
				}
			}

			ca.logoutRequest();
		}

	}

Client cb = new Client();
		if(cb.connectServer()) {
			if(cb.loginRequest("ub", "ub")) {
				cb.receiveUserInformation("ub");
				System.out.println(cb.getMyUser().getName());

				cb.receiveMyQuestion();
				ArrayList<Question> myq = new ArrayList<Question>();
				myq = cb.getMyQuestion();
				for(Question q: myq) {
					ArrayList<User> cand = new ArrayList<User>();
					cand = q.getCandidates();
					if(!cand.isEmpty()) {
						for(User u: cand) {
							System.out.println(q.getQuestion()+"Ç…ëŒÇ∑ÇÈóßåÛï‚é“ÇÕ"+u.getName()+"Ç≈Ç∑");
							cb.sendOffer(u.getName(), q.getQuestion());
						}
					}
				}
			}

			cb.logoutRequest();
		}

Client cc = new Client();
		if(cc.connectServer()) {
			if(cc.loginRequest("uc", "uc")) {
				cc.receiveUserInformation("uc");
				System.out.println(cc.getMyUser().getName());

				cc.receiveOffer();
				ArrayList<Question> myo = new ArrayList<Question>();
				myo = cc.getMyOffer();
				for(Question q: myo) {
					System.out.println(q.getQuestion());
				}
			}
		}

		cc.logoutRequest();


	}
