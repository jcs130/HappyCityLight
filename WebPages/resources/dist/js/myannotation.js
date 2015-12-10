		$(function() {
			$
					.ajax({
						type : "GET",
						url : "annotation/getallrecord",
						data : {},
						dataType : "json",
						success : function(data, textStatus) {
							var num = 1;
							$
									.each(
											data,
											function() {
												$("#annotation-history")
														.append(
																"<tr><td>"
																		+ num
																		+ "</td><td>"
																		+ this.mark_at
																		+ "</td><td>"
																		+ this.text
																		+ "</td><td><span class='label emotion'>"
																		+ this.emotion_text
																		+ "</span></td><td><img src='"+this.media_urls[0]+"' class='annotation-img' alt='Product Image'></td><td><span class='label emotion'>"
																		+ this.emotion_medias[0]
																		+ "</span></td></tr>");
												num = num + 1;
											});

							$.each($(".emotion"), function() {
								switch ($(this).html()) {
								case "positive":
									$(this).addClass("label-success");
									break;
								case "neutral":
									$(this).addClass("label-warning");
									break;
								case "negative":
									$(this).addClass("label-danger");
									break;
								default:
									break;
								}
							});
							
							$("#example1").DataTable({
								"paging" : true,
								"lengthChange" : true,
								"searching" : false,
								"ordering" : false,
								"info" : true,
								"autoWidth" : true
							});

						}
					});
			 

		});