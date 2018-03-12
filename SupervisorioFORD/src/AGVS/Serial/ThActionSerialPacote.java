package AGVS.Serial;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AGVS.Data.AGV;
import AGVS.Data.AlertFalhas;
import AGVS.Data.Cancelas;
import AGVS.Data.ComandoMashSerial;
import AGVS.Data.ConfigProcess;
import AGVS.Data.Cruzamento_OLD;
import AGVS.Data.LogZoneTime;
import AGVS.Data.MeshSerial;
import AGVS.Data.PausablePlayer;
import AGVS.Data.PortaMashSerial;
import AGVS.Data.Tag;
import AGVS.Data.TagAtraso;
import AGVS.Drivers.CLP.PLCComDriver;
import AGVS.FORD.SUPERVISORIO.Logic;
import AGVS.Util.Log;
import AGVS.Util.Util;
import Main.SupervisorioFord;
import PLCCom.ePLCType;
import WebService.http.Config;

public class ThActionSerialPacote extends Thread {

	private Map<Integer, String> status;
	private Map<Integer, String> status_war;
	private String pacote = "";
	private static final String comandoAtualizarPos = "R";
	private static final String comandoAction = "A";
	private static final String comandoStatus = "S";
	private static final String comandoFrequencia = "F";
	private static final String comandoNext = "N";
	private Serial serial;
	private List<TagAtraso> tagsAtraso;
	private static int idFechaCancela = 0;
	private int[] pacoteInt = new int[255];
	private static long valTime = 0;
	// Somente Para Primeiro desenvolvimento.

	public ThActionSerialPacote(String pacote, Serial serial, int[] pacoteInt) {

		status = new HashMap<Integer, String>();
		status_war = new HashMap<Integer, String>();
		for (int i = 0; i < AGV.statusAGV.length; i++) {
			for (int j = 0; j < AGV.statusAGVWar.length; j++) {
				if (AGV.statusAGV[i] == AGV.statusAGVWar[j])
					status_war.put(i + 1, AGV.statusAGV[i]);
			}
			status.put(i + 1, AGV.statusAGV[i]);

		}

		for (int j = 0; j < pacoteInt.length; j++) {
			this.pacoteInt[j] = pacoteInt[j];
		}

		this.pacote = pacote;
		this.serial = serial;
		this.start();

	}

	public void run() {
		Config config = Config.getInstance();
		try {
			System.out.println("PACOTE: " + pacote);
			String comando = Util.localizarStrXML(pacote, "<c>", "</c>");
			switch (comando) {
			case comandoAction:
				Logic.FordAction(pacote);

				break;
			case comandoAtualizarPos:
				System.out.println("comandoAtualizarPos");

				////////////////////////////////////////
				for (AGV agv : DatabaseStatic.lstAGVS) {
					if (agv.getId() == Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>"))) {
						agv.setStatus(status.get(Integer.parseInt(Util.localizarStrXML(pacote, "<s>", "</s>"))));
						agv.setTagAtual(Util.localizarStrXML(pacote, "<t>", "</t>"));
						agv.setTagAtualTime(System.currentTimeMillis());
						agv.setStatusTimeOld(new Date().getTime());
					}
				}
				///////////////////////////////////////
				List<AGV> agvs = ConfigProcess.bd().selecAGVS();
				int j;
				for (j = 0; agvs != null && j < agvs.size(); j++) {
					AGV agv = agvs.get(j);
					if (agv.getId() == Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>"))) {
						String oldStatus = agv.getStatus();
						agv.setStatus(status.get(Integer.parseInt(Util.localizarStrXML(pacote, "<s>", "</s>"))));
						verifyStopByStatus(Integer.parseInt(Util.localizarStrXML(pacote, "<s>", "</s>")));

						if (agv.getTagAtual() == null || !agv.getTagAtual().equals(Util.localizarStrXML(pacote, "<t>", "</t>"))) {
							for (int t = 0; DatabaseStatic.cruzamentos != null
									&& t < DatabaseStatic.cruzamentos.size(); t++) {
								DatabaseStatic.cruzamentos.get(t).verificaCruzamento(agv, Util.localizarStrXML(pacote, "<t>", "</t>"));
							}

							// verificar entradas da mesh
							if (config.getProperty(Config.PROP_PROJ).equals(ConfigProcess.PROJ_FIAT)) {
								for (MeshSerial ms : DatabaseStatic.mashs) {
									switch (ms.getId()) {
									case 1:
										if (Util.localizarStrXML(pacote, "<t>", "</t>").equals("3436")) {
											if (!verifyStatusPort("5", ms.getLstPms())
													&& !verifyStatusPort("6", ms.getLstPms())) {
												AGV.enviarPlay(agv.getEnderecoIP(), agv.getMac64());
												DatabaseStatic.bufferEntradaVazioLinha1 = null;
												System.out.println("Entrada KIT VAZIO LINHA 1 LIBERADA pela Mesh");
											} else {
												AGV.enviarEmFila(agv.getEnderecoIP(), agv.getMac64());
												DatabaseStatic.bufferEntradaVazioLinha1 = agv;
												System.out.println("Entrada KIT VAZIO LINHA 1 NÃO LIBERADA pela Mesh");
											}
										}
										break;
									case 2:
										if (Util.localizarStrXML(pacote, "<t>", "</t>").equals("3338")) {
											if (!verifyStatusPort("5", ms.getLstPms())
													&& !verifyStatusPort("6", ms.getLstPms())) {
												AGV.enviarPlay(agv.getEnderecoIP(), agv.getMac64());
												DatabaseStatic.bufferEntradaVazioLinha2 = null;
												System.out.println("Entrada KIT VAZIO LINHA 2 LIBERADA pela Mesh");
											} else {
												AGV.enviarEmFila(agv.getEnderecoIP(), agv.getMac64());
												DatabaseStatic.bufferEntradaVazioLinha2 = agv;
												System.out.println("Entrada KIT VAZIO LINHA 2 NÃO LIBERADA pela Mesh");
											}

										}
										break;
									case 3:
										if (Util.localizarStrXML(pacote, "<t>", "</t>").equals("3130")) {
											if (verifyStatusPort("5", ms.getLstPms())
													&& verifyStatusPort("6", ms.getLstPms())) {
												AGV.enviarPlay(agv.getEnderecoIP(), agv.getMac64());
												DatabaseStatic.bufferEntradaVazioLinha3 = null;
												System.out.println("Entrada KIT VAZIO LINHA 3 LIBERADA pela Mesh");
											} else {
												AGV.enviarEmFila(agv.getEnderecoIP(), agv.getMac64());
												DatabaseStatic.bufferEntradaVazioLinha3 = agv;
												System.out.println("Entrada KIT VAZIO LINHA 3 NÃO LIBERADA pela Mesh");
											}
										}
										break;
									case 4:
										if (Util.localizarStrXML(pacote, "<t>", "</t>").equals("3333")) {
											if (verifyStatusPort("5", ms.getLstPms())
													&& verifyStatusPort("6", ms.getLstPms())) {
												AGV.enviarPlay(agv.getEnderecoIP(), agv.getMac64());
												DatabaseStatic.bufferEntradaCheioLinha2 = null;
												System.out.println("Entrada KIT CHEIO LINHA 2 LIBERADA pela Mesh");
											} else {
												AGV.enviarEmFila(agv.getEnderecoIP(), agv.getMac64());
												DatabaseStatic.bufferEntradaCheioLinha2 = agv;
												System.out.println("Entrada KIT CHEIO LINHA 2 NÃO LIBERADA pela Mesh");
											}
										}
										break;
									case 5:
										if (Util.localizarStrXML(pacote, "<t>", "</t>").equals("3232")) {
											if (verifyStatusPort("5", ms.getLstPms())
													&& verifyStatusPort("6", ms.getLstPms())) {
												AGV.enviarPlay(agv.getEnderecoIP(), agv.getMac64());
												DatabaseStatic.bufferEntradaCheioLinha3 = null;
												System.out.println("Entrada KIT VAZIO LINHA 3 LIBERADA pela Mesh");
											} else {
												AGV.enviarEmFila(agv.getEnderecoIP(), agv.getMac64());
												DatabaseStatic.bufferEntradaCheioLinha3 = agv;
												System.out.println("Entrada KIT VAZIO LINHA 3 NÃO LIBERADA pela Mesh");
											}
										}
										break;
									default:
										break;
									}
								}
							}

							for (int i = 0; DatabaseStatic.cancelas != null
									&& i < DatabaseStatic.cancelas.size(); i++) {
								DatabaseStatic.cancelas.get(i).cruzamentoCancela(Util.localizarStrXML(pacote, "<t>", "</t>"), agv);
							}

							for (int i = 0; DatabaseStatic.cruzamentoMash != null
									&& i < DatabaseStatic.cruzamentoMash.size(); i++) {

								DatabaseStatic.cruzamentoMash.get(i).cruzamentoCancela(Util.localizarStrXML(pacote, "<t>", "</t>"), agv);
							}
							for (int i = 0; DatabaseStatic.tagCruzamentoMash != null
									&& i < DatabaseStatic.tagCruzamentoMash.size(); i++) {

								DatabaseStatic.tagCruzamentoMash.get(i).cruzamento(Util.localizarStrXML(pacote, "<t>", "</t>"), agv);
							}
							for (int i = 0; DatabaseStatic.zoneTimes != null
									&& i < DatabaseStatic.zoneTimes.size(); i++) {
								DatabaseStatic.zoneTimes.get(i).verificarZoneTime(agv, Util.localizarStrXML(pacote, "<t>", "</t>"));
							}

							List<Tag> tag = ConfigProcess.bd().selecTags(Util.localizarStrXML(pacote, "<t>", "</t>"));
							if (tag != null && tag.size() > 0) {
								ConfigProcess.bd().insertLogTags(System.currentTimeMillis(),
										Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>")),
										tag.get(0).getNome(), Util.localizarStrXML(pacote, "<t>", "</t>"));
							} else {
								ConfigProcess.bd().insertLogTags(System.currentTimeMillis(),
										Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>")), "Sem Cadastro",
										Util.localizarStrXML(pacote, "<t>", "</t>"));
							}

							if (!oldStatus.equals(agv.getStatus())) {
								ConfigProcess.bd().updateAGV(agv.getId(), agv.getStatus());
							}
						} else {
							System.out.println("TAG JÁ PROCESSADA");
						}
					}
				}

				int atraso = 0;

				ConfigProcess.bd().updateAGV(Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>")),
						Util.localizarStrXML(pacote, "<t>", "</t>"), Integer.parseInt(Util.localizarStrXML(pacote, "<b>", "</b>")),
						System.currentTimeMillis(), atraso,
						status.get(Integer.parseInt(Util.localizarStrXML(pacote, "<s>", "</s>"))));

				break;
			case comandoStatus:
				System.out.println("Updating status");
				int vStatus = Integer.parseInt(Util.localizarStrXML(pacote, "<s>", "</s>"));
				// Comando da MashSerial
				if (vStatus >= 100 && vStatus <= 101) {
					System.out.println("Info Cancela: " + vStatus);
					for (int i = 0; DatabaseStatic.cancelas != null && i < DatabaseStatic.cancelas.size(); i++) {
						Cancelas cl = DatabaseStatic.cancelas.get(i);
						int id = Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>"));
						if (cl.getC1().getMs().getId() == id) {
							System.out.println("Cancela 1");
							cl.setStatusC1(vStatus);
						}
						if (cl.getC2().getMs().getId() == id) {
							System.out.println("Cancela 2");
							cl.setStatusC2(vStatus);
						}
					}
					return;
				} else if (vStatus >= 17 && vStatus <= 32) {

					for (MeshSerial ms : DatabaseStatic.mashs) {
						if (ms.getId() == Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>"))) {
							for (PortaMashSerial pms : ms.getLstPms()) {
								if (pms.getNome().equals(PortaMashSerial.getPort(vStatus))) {
									pms.setStatus(PortaMashSerial.getPortState(vStatus));
									ConfigProcess.bd().updatePortIn(pms.getPorta(), ms.getId(),
											PortaMashSerial.getPortState(vStatus));
								}
							}

							// verificar entradas da mesh para liberação de agvs na entrada das linhas
							if (config.getProperty(Config.PROP_PROJ).equals(ConfigProcess.PROJ_FIAT)) {
								switch (ms.getId()) {
								case 1:
									if (!verifyStatusPort("5", ms.getLstPms())
											&& !verifyStatusPort("6", ms.getLstPms())) {

										if (DatabaseStatic.bufferEntradaVazioLinha1 != null) {
											// if (DatabaseStatic.bufferEntradaVazioLinha1.get(0) != null) {
											AGV agv = DatabaseStatic.bufferEntradaVazioLinha1;
											AGV.enviarPlay(agv.getEnderecoIP(), agv.getMac64());
											DatabaseStatic.bufferEntradaVazioLinha1 = null;
											System.out.println(
													"Entrada VAZIO linha 1 AUTORIZADA pela Mesh. AGV: " + agv.getId());
											// }
										}
									}
									break;
								case 2:
									if (!verifyStatusPort("5", ms.getLstPms())
											&& !verifyStatusPort("6", ms.getLstPms())) {

										if (DatabaseStatic.bufferEntradaVazioLinha2 != null) {
											// if (DatabaseStatic.bufferEntradaVazioLinha2.get(0) != null) {
											AGV agv = DatabaseStatic.bufferEntradaVazioLinha2;
											AGV.enviarPlay(agv.getEnderecoIP(), agv.getMac64());
											DatabaseStatic.bufferEntradaVazioLinha2 = null;
											System.out.println(
													"Entrada VAZIO linha 2 AUTORIZADA pela Mesh. AGV: " + agv.getId());
											// }
										}
									}
									break;
								case 3:
									if (verifyStatusPort("5", ms.getLstPms())
											&& verifyStatusPort("6", ms.getLstPms())) {

										if (DatabaseStatic.bufferEntradaVazioLinha3 != null) {
											// if (DatabaseStatic.bufferEntradaVazioLinha3.get(0) != null) {
											AGV agv = DatabaseStatic.bufferEntradaVazioLinha3;
											AGV.enviarPlay(agv.getEnderecoIP(), agv.getMac64());
											DatabaseStatic.bufferEntradaVazioLinha3 = null;
											System.out.println(
													"Entrada VAZIO linha 3 AUTORIZADA pela Mesh. AGV: " + agv.getId());
											// }
										}

									}
									break;
								case 4:
									if (verifyStatusPort("5", ms.getLstPms())
											&& verifyStatusPort("6", ms.getLstPms())) {

										if (DatabaseStatic.bufferEntradaCheioLinha2 != null) {
											// if (DatabaseStatic.bufferEntradaCheioLinha2.get(0) != null) {
											AGV agv = DatabaseStatic.bufferEntradaCheioLinha2;
											AGV.enviarPlay(agv.getEnderecoIP(), agv.getMac64());
											DatabaseStatic.bufferEntradaCheioLinha2 = null;
											System.out.println(
													"Entrada CHEIO linha 2 AUTORIZADA pela Mesh. AGV: " + agv.getId());
											// }
										}
									}
									break;
								case 5:
									if (verifyStatusPort("5", ms.getLstPms())
											&& verifyStatusPort("6", ms.getLstPms())) {

										if (DatabaseStatic.bufferEntradaCheioLinha3 != null) {
											// if (DatabaseStatic.bufferEntradaCheioLinha3.get(0) != null) {
											AGV agv = DatabaseStatic.bufferEntradaCheioLinha3;
											AGV.enviarPlay(agv.getEnderecoIP(), agv.getMac64());
											DatabaseStatic.bufferEntradaCheioLinha3 = null;
											System.out.println(
													"Entrada CHEIO linha 3 AUTORIZADA pela Mesh. AGV: " + agv.getId());
											// }
										}
									}
									break;
								default:
									break;
								}
							}
							///////////////////////////////////////////////////////////////

						}
					}

					if (config.getProperty(Config.PROP_PROJ).equals(ConfigProcess.PROJ_TOYOTA_INDAIATUBA)) {
						for (int k = 0; DatabaseStatic.pms != null && k < DatabaseStatic.pms.size(); k++) {
							PortaMashSerial pms = DatabaseStatic.pms.get(k);
							if (pms.getMs().getId() == Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>"))) {

								if (pms.getPorta().equals(PortaMashSerial.getPort(vStatus))) {
									if (pms.getMs().getId() == 95 || pms.getMs().getId() == 96
											|| pms.getMs().getId() == 50 || pms.getMs().getId() == 105) {
										if (PortaMashSerial.getPortState(vStatus).equals(PortaMashSerial.E1_OFF)
												|| PortaMashSerial.getPortState(vStatus).equals(PortaMashSerial.E2_OFF)
												|| PortaMashSerial.getPortState(vStatus).equals(PortaMashSerial.E3_OFF)
												|| PortaMashSerial.getPortState(vStatus).equals(PortaMashSerial.E4_OFF)
												|| PortaMashSerial.getPortState(vStatus).equals(PortaMashSerial.E5_OFF)
												|| PortaMashSerial.getPortState(vStatus).equals(PortaMashSerial.E6_OFF)
												|| PortaMashSerial.getPortState(vStatus).equals(PortaMashSerial.E7_OFF)
												|| PortaMashSerial.getPortState(vStatus)
														.equals(PortaMashSerial.E8_OFF)) {
											Thread.sleep(3000);
										}
									}
									pms.setStatus(PortaMashSerial.getPortState(vStatus));
									System.out.println(pms.getMs().getId() + ": " + pms.getStatus());
								}

							}
						}
						for (int k = 0; DatabaseStatic.pms != null && k < DatabaseStatic.pms.size(); k++) {
							PortaMashSerial pms = DatabaseStatic.pms.get(k);
							if (pms.getMs().getId() == Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>"))) {

								System.out.println(pms.getMs().getId() + ": " + pms.getStatus());

							}
						}
					}
					return;
				} else if (status.containsKey(vStatus)) {
					System.out.println(status.get(vStatus));
					////////////////////////////////////////////////////////
					for (AGV agv : DatabaseStatic.lstAGVS) {
						if (agv.getId() == Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>"))) {
							agv.setStatus(status.get(vStatus));
							agv.setStatusTimeOld(System.currentTimeMillis());
						}
					}
					////////////////////////////////////////////////////////

					List<AGV> agvs2 = ConfigProcess.bd().selecAGVS();
					AGV agv = null;
					for (j = 0; agvs2 != null && j < agvs2.size(); j++) {
						agv = agvs2.get(j);
						if (agv.getId() == Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>"))) {

							agv.setStatus(status.get(vStatus));
							for (int t = 0; DatabaseStatic.cruzamentos != null
									&& t < DatabaseStatic.cruzamentos.size(); t++) {
								DatabaseStatic.cruzamentos.get(t).execLiberaVerificaAGV(agv);
							}
							for (int i = 0; DatabaseStatic.tagCruzamentoMash != null
									&& i < DatabaseStatic.tagCruzamentoMash.size(); i++) {
								DatabaseStatic.tagCruzamentoMash.get(i).cruzamento(agv);
							}
							for (int i = 0; DatabaseStatic.cancelas != null
									&& i < DatabaseStatic.cancelas.size(); i++) {
								DatabaseStatic.cancelas.get(i).cruzamentoCancela(agv);
							}
							for (int i = 0; DatabaseStatic.cruzamentoMash != null
									&& i < DatabaseStatic.cruzamentoMash.size(); i++) {
								DatabaseStatic.cruzamentoMash.get(i).cruzamentoCancela(agv);
							}

							/*
							 * VERIFICA SE O STATUS ATUAL É IGUAL AO ÚLTIMO STATUS - EVITAR STATUS REPETIDO
							 * EM SEQUENCIA
							 */
							String msg = status.get(vStatus);
							if (!agv.getStatus().equals(msg)) {
								ConfigProcess.bd().insertFalhas(
										Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>")), msg,
										System.currentTimeMillis());
							}
							///////////////////////////////////////////////////////////////////////////////////////////////
						}
					}

					ConfigProcess.bd().updateAGV(Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>")),
							status.get(vStatus));
					if (status_war.containsKey(vStatus)) {
						ConfigProcess.bd().updateAGV(Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>")),
								status_war.get(vStatus), System.currentTimeMillis());
					}
					verifyStopByStatus(vStatus);
				}

				break;
			case comandoFrequencia:
				List<AGV> agvs3 = ConfigProcess.bd().selecAGVS();

				int iFrequencia = Integer.parseInt(Util.localizarStrXML(pacote, "<f>", "</f>"));
				int iAgv = Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i>"));

				int aux;
				for (aux = 0; agvs3 != null && aux < agvs3.size(); aux++) {
					AGV agv = agvs3.get(aux);
					if (agv.getId() == iAgv) {
						ConfigProcess.bd().updateAGVFrequency(iAgv, iFrequencia);
						DatabaseStatic.resetAGVS();
					}
				}

				break;
			default:
				break;
			}
		} catch (Exception e) {
			new Log(e);
		}
	}

	private void verifyStopByStatus(int vStatus) {
		// VERIFICA SE ELE ESTÁ DENTRO DE UMA ZONA DE TEMPO
		for (LogZoneTime lzt : DatabaseStatic.logZoneTimes) {
			if (lzt.getAgv().getId() == Integer.parseInt(Util.localizarStrXML(pacote, "<i>", "</i"))) {
				if (status.get(vStatus) == AGV.statusObstaculo) {
					System.out.println("Obstaculo em zona de tempo");
					lzt.setObstacle(true);
				} else {
					lzt.setObstacle(false);
				}
				break;
			}
		}
	}

	private boolean verifyStatusPort(String port, List<PortaMashSerial> lstPms) {
		for (PortaMashSerial pms : lstPms) {
			if (!pms.getPorta().equals(null) && pms.getPorta().equals(port)) {
				if (pms.getStatus() != null)
					if (pms.getStatus().equals(pms.getAcionamento()))
						return true;
				return false;
			}

		}
		return false;
	}

}
