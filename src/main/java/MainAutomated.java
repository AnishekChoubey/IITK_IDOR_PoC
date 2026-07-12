public class MainAutomated implements IdorPollingSystem.Pollable<UserDatIITK>{

    public static void main(String[] args)throws Exception{
        MainAutomated mainAutomated = new MainAutomated();
        IdorPollingSystem<UserDatIITK> poller = new IdorPollingSystem<>(
                1,3500,64,
                mainAutomated
        );
        poller.startPolling();
    }

    @Override
    public UserDatIITK fetchStockDataFromServer(int idorId) {

        try {
            UserDatIITK f = Fetcher.fetchInfo(idorId);
            System.out.println(f.applicant_name+","+f.email_id+","+f.mobile_no);
            return f;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public boolean shouldStop(int idorId, UserDatIITK data) {
        return false;
    }
}
