import { motion } from "framer-motion";

interface SplashPageProps {
  nome: string | undefined;
  onComplete: () => void;
}

const SplashPage = ({ nome }: SplashPageProps) => {
  return (
    <>
    <div className="flex h-screen items-center justify-center">
      <div className="text-center z-10 relative">

        <motion.h1
          initial={{ x: "-100vw", opacity: 0 }}
          animate={{ x: 0, opacity: 1 }}
          transition={{ type: "spring", stiffness: 100, duration: 2 }}
          className="text-3xl font-bold text-white"
        >
          Olá, {nome}!
        </motion.h1>

        <motion.p
          initial={{ x: "100vw", opacity: 0 }}
          animate={{ x: 0, opacity: 1 }}
          transition={{
            type: "spring",
            stiffness: 100,
            duration: 2,
            delay: 0.5,
          }}
          className="mt-4 text-xl text-yellow-400"
        >
          Seja bem-vindo à <strong>Realiza Assessoria</strong>.
        </motion.p>

        <motion.p
          initial={{ x: "100vw", opacity: 0 }}
          animate={{ x: 0, opacity: 1 }}
          transition={{ type: "spring", stiffness: 100, duration: 2, delay: 1 }}
          className="mt-2 text-xl text-white"
        >
          Preparado para manter sua empresa em dia?
        </motion.p>
      </div>
    </div>
    </>
  );
};

export default SplashPage;
