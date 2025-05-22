import { ScrollArea } from "@/components/ui/scroll-area";
import { useBranch } from "@/context/Branch-provider";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";

export function ServicesSection() {
  const { selectedBranch } = useBranch();
  const [services, setServices] = useState([]);

  const getServices = async () => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      const res = await axios.get(`${ip}/contract/service-type`, {
        params: {
          owner: "BRANCH",
          idOwner: selectedBranch?.idBranch,
        },
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      console.log(res.data);

      setServices(res.data);
    } catch (err: any) {
      console.log(err);
    }
  };

  useEffect(() => {
    if (selectedBranch?.idBranch) {
      getServices();
    }
  }, [selectedBranch?.idBranch]);

  return (
    <div className="relative bottom-[8vw] ">
      <ScrollArea className="h-[50vh] w-[20vw]">
        <div>
          {Array.isArray(services) &&
            services.map((service: any) => (
              <div key={service.idServiceType}>{service.title}</div>
            ))}
        </div>
      </ScrollArea>
    </div>
  );
}
