				var options = {
					manipulation: {
					  addNode: function (data, callback) {
						// filling in the popup DOM elements
						document.getElementById('operation').innerHTML = "Add Node";
						document.getElementById('node-id').value = data.id;
						document.getElementById('node-uuid').value = "";
						document.getElementById('node-label').value = "";
						document.getElementById('node-extract-fields').value = "";
						document.getElementById('node-group').value = data.group;
						document.getElementById('saveButton').onclick = saveNodeData.bind(this, data, callback);
						document.getElementById('cancelButton').onclick = clearPopUp.bind();
						document.getElementById('network-popUp').style.display = 'block';
					  },
					  editNode: function (data, callback) {
						// filling in the popup DOM elements
						document.getElementById('operation').innerHTML = "Edit Node";
						document.getElementById('node-id').value = data.id;
						document.getElementById('node-uuid').value = data.uuid;
						document.getElementById('node-label').value = data.label;
						document.getElementById('node-extract-fields').value = data.extractFields;
						document.getElementById('node-group').value = data.group;
						document.getElementById('saveButton').onclick = saveNodeData.bind(this, data, callback);
						document.getElementById('cancelButton').onclick = cancelEdit.bind(this,callback);
						document.getElementById('network-popUp').style.display = 'block';
					  },
					  addEdge: function (data, callback) {
						if (data.from == data.to) {
						  var r = confirm("Do you want to connect the node to itself?");
						  if (r != true) {
							callback(null);
							return;
						  }
						}
						  document.getElementById('edge-operation').innerHTML = "Edit Edge";
						  document.getElementById('edge-label').value = "";
						  document.getElementById('edge-extract-fields').value = "";
						  document.getElementById('edge-saveButton').onclick = saveEdgeData.bind(this, data, callback);
						  document.getElementById('edge-cancelButton').onclick = cancelEdgeEdit.bind(this,callback);
						  document.getElementById('edge-popUp').style.display = 'block';
					  },
					  editEdge: {
						editWithoutDrag: function(data, callback) {
							var cEdge = edges.get(data.id);
							document.getElementById('edge-operation').innerHTML = "Edit Edge";
							document.getElementById('edge-label').value = cEdge.label;
							document.getElementById('edge-extract-fields').value = cEdge.extractFields;
							document.getElementById('edge-saveButton').onclick = saveEdgeData.bind(this, data, callback);
							document.getElementById('edge-cancelButton').onclick = cancelEdgeEdit.bind(this,callback);
							document.getElementById('edge-popUp').style.display = 'block';		  
						}
          }					  

					  
					},				
        nodes: {
            shape: 'dot',
            size: 20,
            font: {
                size: 18,
                color: 'black'
            },
            borderWidth: 2
        },
        edges: {
            width: 2,
			    smooth: {
					forceDirection: 'none'
				}
        },
        groups: {
            squad: {
                shape: 'icon',
                icon: {
                    face: 'FontAwesome',
                    code: '\uf0c0',
                    size: 50,
                    color: 'orange'
                }
            },		
            ba: {
                shape: 'icon',
                icon: {
                    face: 'FontAwesome',
                    code: '\uf007',
                    size: 50,
                    color: 'green'
                }
            },		
            wso: {
                shape: 'icon',
                icon: {
                    face: 'FontAwesome',
                    code: '\uf234',
                    size: 50,
                    color: '#00BFFF'
                }
            },				
            po: {
                shape: 'icon',
                icon: {
                    face: 'FontAwesome',
                    code: '\uf007',
                    size: 50,
                    color: '#1E90FF'
                }
            },		
           dev: {
                shape: 'icon',
                icon: {
                    face: 'FontAwesome',
                    code: '\uf007',
                    size: 50,
                    color: '#A0522D'
                }
            },		
           qa: {
                shape: 'icon',
                icon: {
                    face: 'FontAwesome',
                    code: '\uf007',
                    size: 50,
                    color: '#F08080'
                }
            },		

			company_goal: {
                shape: 'icon',
                icon: {
                    face: 'FontAwesome',
                    code: '\uf0c2',
                    size: 80,
                    color: '#00BFFF'
                }
            },					
			tribe_objective: {
                shape: 'icon',
                icon: {
                    face: 'FontAwesome',
                    code: '\uf14e',
                    size: 60,
                    color: '#8A2BE2'
                }
            },					
			squad_objective: {
                shape: 'icon',
                icon: {
                    face: 'FontAwesome',
                    code: '\uf11e',
                    size: 50,
                    color: '#9932CC'
                }
            },		
			key_result: {
                shape: 'icon',
                icon: {
                    face: 'FontAwesome',
                    code: '\uf11d',
                    size: 50,
                    color: '#9932CC'
                }
            },			
			strategy: {
                shape: 'icon',
                icon: {
                    face: 'FontAwesome',
                    code: '\uf007',
                    size: 50,
                    color: 'black'
                }
            },	
            source: {
                color:{border:'white'}
            }
        }					
				  };				