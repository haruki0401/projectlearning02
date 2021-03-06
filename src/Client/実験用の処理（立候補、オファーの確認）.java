Client ca = new Client();
		if(ca.connectServer()) {
			if(ca.accountRequest("uc", "uc", "さんにんめ")) {
				ArrayList<String> as = new ArrayList<String>();
				as.add("東京大学");
				as.add("横浜国立大学");
				ca.sendUserInformation("学生", "大学生", as);

				ca.receiveUserInformation("uc");

				ca.Candidacy("質問０");
				ca.Candidacy("質問１");

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
							System.out.println(q.getQuestion()+"に対する立候補者は"+u.getName()+"です");
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
